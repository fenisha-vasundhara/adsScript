# 16 KB page alignment

Android 13+ devices ship with 16 KB memory pages on some ARM64 builds. Any ELF we distribute **must** align every `PT_LOAD` segment to at least 0x4000 (16 KB) or the loader will reject it. The project now enforces this requirement in three places.

## Build flags

- **CMake:** `app/CMakeLists.txt` unconditionally adds `-Wl,-z,max-page-size=0x4000` and `-Wl,-z,common-page-size=0x4000` whenever we build for Android. Every native target linked through CMake inherits the flags.
- **ndk-build:** We do not currently ship ndk-build modules, but new modules must append the same flags to `LOCAL_LDFLAGS` (`-Wl,-z,max-page-size=0x4000 -Wl,-z,common-page-size=0x4000`).
- **Prebuilt/vendor .so:** `merge*NativeLibs` and `strip*DebugSymbols` now patch every packaged `.so` to bump any `PT_LOAD` alignment < 0x4000 up to 0x4000 before the APK is assembled. Still prefer true relinks when possible so we do not diverge from upstream, but the build guarantees the final binaries are safe.

We stay on the LLVM lld linker that ships with the NDK; no additional toolchain tweaks are required.

## Verification

1. Build the release APK (`./gradlew :app:assembleRelease`).
2. Run `bash tools/check-16k-pages.sh app/build/outputs/apk/release/app-release.apk`.
3. The script unzips the APK, checks every `lib/<abi>/*.so` with `readelf`, and fails if any `PT_LOAD` segment aligns to less than 16384 bytes.

The checker looks for `readelf`/`llvm-readelf` on `PATH`, then falls back to the usual `$ANDROID_NDK_HOME`/`$ANDROID_SDK_ROOT/ndk/*` toolchains. Export `READELF_BIN=/absolute/path/to/readelf` if you need to force a specific binary.

CI integration: `:app:assembleRelease` now finalizes `:app:checkRelease16kPages`, which invokes the same script automatically. Any failing library breaks the build. `:app:bundleRelease` similarly finalizes `:app:checkReleaseBundle16kPages` after padding the bundle.

## Bundle ZIP alignment

Google Play also validates that each `base/lib/<abi>/*.so` entry inside the App Bundle begins on a 16 KB ZIP offset.
The new `tools/align-bundle-native-libs.py` script pads the local header extra field so that the first byte of the
payload is aligned to 16 KB without changing the library contents. `:alignReleaseBundleNativeLibs` automatically runs
after `:bundleRelease`, but you can align bundles manually like this:

```
python3 tools/align-bundle-native-libs.py --dry-run app/build/outputs/bundle/release/app-release.aab
python3 tools/align-bundle-native-libs.py app/build/outputs/bundle/release/app-release.aab
```

The first command reports every misaligned `.so`, and the second rewrites the bundle in-place.

## Manual spot checks

```
readelf -l path/to/libfoo.so | grep -A1 LOAD
```

Every `LOAD` record must end with `Align 0x4000` (or a larger alignment).

## Tracking prebuilts

If a vendor library cannot be relinked, document the upstream and the steps taken to obtain a compliant build. Store notes alongside this README so the release checklist stays reproducible. Until the vendor delivers a 16 KB build we must treat the library as a blocker for release.

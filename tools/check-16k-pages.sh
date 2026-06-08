#!/usr/bin/env bash
set -euo pipefail

shopt -s nullglob

READELF_BIN="${READELF_BIN:-${READELF:-}}"
if [[ -z "$READELF_BIN" ]]; then
  for candidate in readelf llvm-readelf aarch64-linux-android-readelf arm-linux-androideabi-readelf; do
    if command -v "$candidate" >/dev/null 2>&1; then
      READELF_BIN="$candidate"
      break
    fi
  done
fi

if [[ -z "$READELF_BIN" ]]; then
  declare -a ndk_roots=()
  for var in ANDROID_NDK_HOME ANDROID_NDK_ROOT NDK_HOME NDK_ROOT; do
    value="${!var:-}"
    if [[ -n "$value" ]]; then
      ndk_roots+=("$value")
    fi
  done

  for var in ANDROID_SDK_ROOT ANDROID_HOME; do
    sdk="${!var:-}"
    if [[ -n "$sdk" ]]; then
      for dir in "$sdk"/ndk/* "$sdk"/ndk-bundle; do
        if [[ -d "$dir" ]]; then
          ndk_roots+=("$dir")
        fi
      done
    fi
  done

  # Some bash versions with `set -u` treat expanding an empty array as unbound.
  # Guard the loop to keep the script portable.
  if ((${#ndk_roots[@]} > 0)); then
    for root in "${ndk_roots[@]}"; do
      for candidate in \
        "$root"/toolchains/llvm/prebuilt/*/bin/llvm-readelf \
        "$root"/toolchains/llvm/prebuilt/*/bin/readelf \
        "$root"/toolchains/llvm/prebuilt/*/bin/*-readelf; do
        if [[ -x "$candidate" ]]; then
          READELF_BIN="$candidate"
          break 2
        fi
      done
    done
  fi
fi

if [[ -z "$READELF_BIN" ]]; then
  if [[ "${STRICT_16K_CHECK:-0}" == "1" || "${CI:-}" == "true" ]]; then
    echo "No readelf-compatible tool found. Install binutils, use the NDK's llvm-readelf, or export READELF_BIN=/path/to/readelf." >&2
    exit 1
  fi
  echo "Skipping 16 KB page validation: no readelf-compatible tool found. Set STRICT_16K_CHECK=1 to fail instead." >&2
  exit 0
fi

if ! command -v "$READELF_BIN" >/dev/null 2>&1 && [[ ! -x "$READELF_BIN" ]]; then
  echo "Configured readelf binary '$READELF_BIN' is not executable." >&2
  exit 1
fi

APK="${1:-app/build/outputs/apk/release/app-release.apk}"

if [[ ! -f "$APK" ]]; then
  echo "APK not found: $APK" >&2
  exit 1
fi

WORKDIR="$(mktemp -d)"
trap 'rm -rf "$WORKDIR"' EXIT

unzip -oq "$APK" -d "$WORKDIR"

fail=0
checked_any=0
while IFS= read -r -d '' so; do
  checked_any=1
  max_align=$("$READELF_BIN" -l "$so" \
    | awk '/LOAD/ {print $NF}' \
    | sed 's/0x//' \
    | awk 'NF {printf "%d\n", "0x"$1}' \
    | sort -nr | head -1)

  max_align="${max_align:-0}"

  if [[ "$max_align" -lt 16384 ]]; then
    echo "❌ $so has PT_LOAD align=$max_align (< 16384)"
    fail=1
  else
    echo "✅ $so align=$max_align"
  fi
done < <(find "$WORKDIR" -type f -path "*/lib/*.so" -print0)

if [[ "$checked_any" -eq 0 ]]; then
  echo "No native libraries found under lib/ in $APK"
fi

exit "$fail"

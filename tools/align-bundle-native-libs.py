#!/usr/bin/env python3
"""
Align native libraries in an Android App Bundle so every .so file begins on a 16 KB multiple.

Play Store validation now considers the raw ZIP offsets of `base/lib/<abi>/*.so` inside the bundle.
This script pads the per-entry extra field as needed so that the first byte of each library payload
lands on a 16 KB boundary without touching the library data itself.

Usage:
  python3 tools/align-bundle-native-libs.py <bundle.aab>      # rewrites <bundle.aab> in-place
  python3 tools/align-bundle-native-libs.py --dry-run ...    # reports misaligned libraries
  python3 tools/align-bundle-native-libs.py -o aligned.aab ... # writes to aligned.aab
"""

import argparse
import copy
import math
import sys
import tempfile
import zipfile
from pathlib import Path
from typing import List, Optional, Tuple


DEFAULT_ALIGNMENT = 16 * 1024


def should_align(info: zipfile.ZipInfo) -> bool:
    """Only .so files inside a lib/ directory need 16 KB ZIP alignment."""
    return info.filename.endswith(".so") and "/lib/" in info.filename


def compute_padding(current_offset: int, name_len: int, extra_len: int, align: int) -> int:
    """Return how many extra bytes must be tacked onto the header to hit the alignment."""
    header_len = 30 + name_len + extra_len
    data_start = current_offset + header_len
    target = math.ceil(data_start / align) * align
    return target - data_start


def report_misaligned(bundle_path: Path, align: int) -> List[Tuple[str, int]]:
    """Return a list of (entry, data_offset) for every library that is misaligned."""
    misaligned: List[Tuple[str, int]] = []
    with zipfile.ZipFile(bundle_path, "r") as zf:
        for info in zf.infolist():
            if not should_align(info):
                continue
            header_len = 30 + len(info.filename.encode("utf-8")) + len(info.extra or b"")
            data_offset = info.header_offset + header_len
            if data_offset % align != 0:
                misaligned.append((info.filename, data_offset))
    return misaligned


def align_bundle(input_path: Path, output_path: Path, align: int) -> tuple[int, int]:
    """Repackage the bundle so that each native library payload begins on *align* bytes."""
    aligned = 0
    padded_bytes = 0
    with zipfile.ZipFile(input_path, "r") as src, zipfile.ZipFile(
        output_path, "w", allowZip64=True
    ) as dst:
        dst.comment = src.comment
        for info in src.infolist():
            data = src.read(info.filename)
            new_info = copy.copy(info)
            new_info.compress_type = info.compress_type
            new_info.extra = info.extra or b""
            if should_align(info):
                current_offset = dst.fp.tell()
                name_len = len(info.filename.encode("utf-8"))
                pad = compute_padding(current_offset, name_len, len(new_info.extra), align)
                if pad > 0:
                    new_info.extra = (info.extra or b"") + (b"\x00" * pad)
                    aligned += 1
                    padded_bytes += pad
            dst.writestr(new_info, data)
    return aligned, padded_bytes


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("bundle", type=Path, help="Path to the App Bundle (.aab)")
    parser.add_argument(
        "--align",
        "-a",
        type=int,
        default=DEFAULT_ALIGNMENT,
        help="Alignment in bytes that every native lib payload must reside on",
    )
    parser.add_argument(
        "--output",
        "-o",
        type=Path,
        help="Write the aligned bundle to this path instead of writing in-place",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Report misaligned libraries without performing any rewrite",
    )
    args = parser.parse_args()

    bundle_path = args.bundle.expanduser()
    if not bundle_path.exists():
        parser.error(f"Bundle not found: {bundle_path}")

    if args.dry_run:
        misaligned = report_misaligned(bundle_path, args.align)
        if misaligned:
            print("Native libraries not aligned:")
            for name, offset in misaligned:
                print(f"  {name} offset={offset}")
            print(f"Found {len(misaligned)} misaligned libraries.")
            return 1
        print("All native libraries are already aligned.")
        return 0

    target_path: Optional[Path] = None
    if args.output:
        target_path = args.output.expanduser()
        target_path.parent.mkdir(parents=True, exist_ok=True)
        align_target = target_path
    else:
        temp = tempfile.NamedTemporaryFile(
            prefix=bundle_path.name + ".", suffix=".aligned", dir=bundle_path.parent, delete=False
        )
        temp.close()
        align_target = Path(temp.name)

    try:
        aligned, padded = align_bundle(bundle_path, align_target, args.align)
    except Exception:
        if align_target.exists():
            align_target.unlink()
        raise
    else:
        if args.output:
            print(
                f"Wrote aligned bundle to {target_path} ({aligned} libs padded, {padded} bytes added)."
            )
        else:
            align_target.replace(bundle_path)
            print(
                f"Aligned {aligned} native libs in-place ({padded} bytes of padding added)."
            )
    return 0


if __name__ == "__main__":
    sys.exit(main())

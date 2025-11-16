#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
echo "Project root: $ROOT_DIR"

cd "$ROOT_DIR"

echo "Building shared frameworks for iOS..."
./gradlew :shared:assemble

SIM_FRAMEWORK="$ROOT_DIR/shared/build/bin/iosX64/debugFramework/shared.framework"
DEVICE_FRAMEWORK="$ROOT_DIR/shared/build/bin/iosArm64/debugFramework/shared.framework"
OUT_XCFRAMEWORK="$ROOT_DIR/shared/build/bin/shared.xcframework"

if [ -d "$SIM_FRAMEWORK" ] && [ -d "$DEVICE_FRAMEWORK" ]; then
  echo "Creating XCFramework..."
  xcodebuild -create-xcframework \
    -framework "$SIM_FRAMEWORK" \
    -framework "$DEVICE_FRAMEWORK" \
    -output "$OUT_XCFRAMEWORK"
  echo "XCFramework created at: $OUT_XCFRAMEWORK"

  DEST_DIR="$ROOT_DIR/iosApp/Humanness_iOS/Frameworks"
  mkdir -p "$DEST_DIR"
  cp -R "$OUT_XCFRAMEWORK" "$DEST_DIR/"
  echo "Copied XCFramework to $DEST_DIR"
else
  echo "Could not find built frameworks. Look under shared/build/bin/ for available frameworks." >&2
  exit 1
fi

echo "Done. In Xcode add the XCFramework (or framework) to your app target and set Embed & Sign."

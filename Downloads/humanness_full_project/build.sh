#!/bin/bash

# Humanness App Build Script
# Builds the Android APK for the Humanness Kotlin Multiplatform project

set -e

echo "================================"
echo "Humanness App Build Script"
echo "================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if gradle exists
if ! command -v ./gradlew &> /dev/null; then
    echo -e "${YELLOW}Gradle wrapper not found. Using gradle command...${NC}"
    GRADLE_CMD="gradle"
else
    GRADLE_CMD="./gradlew"
fi

# Parse arguments
BUILD_TYPE="${1:-debug}"

case $BUILD_TYPE in
    debug)
        echo -e "${GREEN}Building Debug APK...${NC}"
        $GRADLE_CMD clean assembleDebug
        echo ""
        echo -e "${GREEN}Debug APK built successfully!${NC}"
        echo "Location: androidApp/build/outputs/apk/debug/"
        ;;
    release)
        echo -e "${GREEN}Building Release APK...${NC}"
        $GRADLE_CMD clean assembleRelease
        echo ""
        echo -e "${GREEN}Release APK built successfully!${NC}"
        echo "Location: androidApp/build/outputs/apk/release/"
        ;;
    clean)
        echo -e "${GREEN}Cleaning build directory...${NC}"
        $GRADLE_CMD clean
        echo -e "${GREEN}Clean completed!${NC}"
        ;;
    install)
        echo -e "${GREEN}Building and installing Debug APK...${NC}"
        $GRADLE_CMD clean installDebug
        echo -e "${GREEN}APK installed successfully!${NC}"
        ;;
    *)
        echo "Usage: $0 {debug|release|clean|install}"
        echo ""
        echo "Examples:"
        echo "  $0 debug    - Build debug APK (default)"
        echo "  $0 release  - Build release APK"
        echo "  $0 clean    - Clean build directory"
        echo "  $0 install  - Build and install debug APK to device"
        exit 1
        ;;
esac

echo ""
echo "================================"
echo "Build Complete!"
echo "================================"

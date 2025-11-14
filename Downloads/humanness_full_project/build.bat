@echo off
REM Humanness App Build Script for Windows
REM Builds the Android APK for the Humanness Kotlin Multiplatform project

echo ================================
echo Humanness App Build Script
echo ================================
echo.

setlocal enabledelayedexpansion

set BUILD_TYPE=%1
if "%BUILD_TYPE%"=="" (
    set BUILD_TYPE=debug
)

if "%BUILD_TYPE%"=="debug" (
    echo Building Debug APK...
    call gradlew.bat clean assembleDebug
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo Debug APK built successfully!
        echo Location: androidApp\build\outputs\apk\debug\
    ) else (
        echo Build failed!
        exit /b 1
    )
) else if "%BUILD_TYPE%"=="release" (
    echo Building Release APK...
    call gradlew.bat clean assembleRelease
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo Release APK built successfully!
        echo Location: androidApp\build\outputs\apk\release\
    ) else (
        echo Build failed!
        exit /b 1
    )
) else if "%BUILD_TYPE%"=="clean" (
    echo Cleaning build directory...
    call gradlew.bat clean
    if %ERRORLEVEL% EQU 0 (
        echo Clean completed!
    ) else (
        echo Clean failed!
        exit /b 1
    )
) else if "%BUILD_TYPE%"=="install" (
    echo Building and installing Debug APK...
    call gradlew.bat clean installDebug
    if %ERRORLEVEL% EQU 0 (
        echo APK installed successfully!
    ) else (
        echo Installation failed!
        exit /b 1
    )
) else (
    echo Usage: %0 [debug^|release^|clean^|install]
    echo.
    echo Examples:
    echo   %0 debug    - Build debug APK (default)
    echo   %0 release  - Build release APK
    echo   %0 clean    - Clean build directory
    echo   %0 install  - Build and install debug APK to device
    exit /b 1
)

echo.
echo ================================
echo Build Complete!
echo ================================

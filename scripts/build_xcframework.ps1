Param()
$ErrorActionPreference = 'Stop'
$Root = Split-Path -Parent $PSScriptRoot
Write-Host "Project root: $Root"
Set-Location $Root

Write-Host "Building shared frameworks for iOS..."
& .\gradlew.bat :shared:assemble

$sim = Join-Path $Root "shared\build\bin\iosX64\debugFramework\shared.framework"
$device = Join-Path $Root "shared\build\bin\iosArm64\debugFramework\shared.framework"
$out = Join-Path $Root "shared\build\bin\shared.xcframework"

if ((Test-Path $sim) -and (Test-Path $device)) {
    Write-Host "Creating XCFramework..."
    & xcodebuild -create-xcframework -framework $sim -framework $device -output $out
    Write-Host "XCFramework created at: $out"

    $dest = Join-Path $Root "iosApp\Humanness_iOS\Frameworks"
    if (!(Test-Path $dest)) { New-Item -ItemType Directory -Path $dest | Out-Null }
    Copy-Item -Recurse -Force $out $dest
    Write-Host "Copied XCFramework to $dest"
} else {
    Write-Error "Could not find built frameworks. Look under shared/build/bin/ for available frameworks."
}

Write-Host "Done. In Xcode add the XCFramework (or framework) to your app target and set Embed & Sign."
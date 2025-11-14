# GitHub Setup Guide

This guide will help you upload the Humanness project to GitHub.

## Prerequisites

- GitHub account
- Git installed on your machine
- Project cloned or ready to push

## Step 1: Create a New Repository on GitHub

1. Go to [github.com](https://github.com)
2. Click on the **+** icon in the top right corner
3. Select **New repository**
4. Fill in the details:
   - **Repository name**: `humanness-kmm`
   - **Description**: "Kotlin Multiplatform + Compose Multiplatform prototype for recording tasks"
   - **Public**: Make sure this is checked (required for submission)
   - **Add .gitignore**: Select "Kotlin"
   - **Add a license**: Select "MIT License" or "Apache 2.0"
5. Click **Create repository**

## Step 2: Initialize Git (If Not Already Done)

Navigate to your project directory and run:

```bash
cd c:\Users\MUDABBIR\Downloads\humanness_full_project
git init
git add .
git commit -m "Initial commit: Humanness KMM + CMP prototype"
```

## Step 3: Add Remote Repository

```bash
git remote add origin https://github.com/YOUR_USERNAME/humanness-kmm.git
```

Replace `YOUR_USERNAME` with your actual GitHub username.

## Step 4: Push to GitHub

```bash
git branch -M main
git push -u origin main
```

You may be prompted to enter your GitHub credentials.

## Step 5: Verify Upload

Visit `https://github.com/YOUR_USERNAME/humanness-kmm` to verify the repository is public and all files are uploaded.

## Building the APK

### On Windows:
```bash
.\build.bat debug
# or for release
.\build.bat release
```

### On Mac/Linux:
```bash
chmod +x build.sh
./build.sh debug
# or for release
./build.sh release
```

The APK will be generated at:
- Debug: `androidApp/build/outputs/apk/debug/androidApp-debug.apk`
- Release: `androidApp/build/outputs/apk/release/androidApp-release.apk`

## Adding APK to GitHub Release

### Create a Release:

1. Go to your GitHub repository
2. Click on **Releases** (right sidebar)
3. Click **Create a new release**
4. Fill in:
   - **Tag version**: `v1.0.0`
   - **Release title**: `Humanness v1.0.0`
   - **Description**: Add features and build information
5. Click **Attach binaries** and upload the APK
6. Click **Publish release**

## Sharing with Submitter

Once everything is uploaded:

1. Copy your repository URL: `https://github.com/YOUR_USERNAME/humanness-kmm`
2. Share the GitHub link
3. Link to the APK can be found in the Releases section

## Project Structure on GitHub

```
humanness-kmm/
├── .github/
│   └── workflows/        # Optional: CI/CD workflows
├── androidApp/
│   ├── src/
│   ├── build.gradle.kts
│   └── ...
├── shared/
│   ├── src/
│   ├── build.gradle.kts
│   └── ...
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── build.sh
├── build.bat
└── .gitignore
```

## Important Files to Commit

- ✅ `build.gradle.kts` files
- ✅ `AndroidManifest.xml`
- ✅ All Kotlin source files in `src/`
- ✅ `README.md`
- ✅ `build.sh` and `build.bat`
- ❌ `build/` directory (ignored)
- ❌ `.gradle/` directory (ignored)
- ❌ `.idea/` directory (ignored)

## Troubleshooting

### Git push fails with authentication error
```bash
# Use personal access token
git remote set-url origin https://YOUR_TOKEN@github.com/YOUR_USERNAME/humanness-kmm.git
git push -u origin main
```

### Large file issues
If you encounter file size issues with APK files, you can use Git LFS:
```bash
git lfs install
git lfs track "*.apk"
git add .gitattributes
```

### Gradle files too large
The gradle cache can be large. Make sure `.gradle/` is in `.gitignore`.

## Final Checklist

Before submitting:

- [ ] Repository is public
- [ ] All source code is committed
- [ ] README.md is complete and accurate
- [ ] APK is built and tested
- [ ] APK is attached to a GitHub Release
- [ ] Repository URL is ready to share
- [ ] Git history is clean

## Additional Resources

- [GitHub Docs - Creating a Repository](https://docs.github.com/en/get-started/quickstart/create-a-repo)
- [GitHub Docs - Managing Releases](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository)
- [Git Handbook](https://guides.github.com/introduction/git-handbook/)

---

**Created**: November 2025  
For more information, refer to the main README.md file.

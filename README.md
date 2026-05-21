# innocence

`innocence` is a local Android music diary app built with Kotlin and Jetpack Compose.

Users can record a song they heard on a certain day, including:

- date
- mood
- song title
- artist
- lyric snippet
- personal reflection

The app also supports lightweight personalization:

- custom background image
- background crop and blur
- theme color switching
- theme color extraction from background image
- card transparency adjustment

## Features

- Home screen with today card and recent history
- New entry flow for song metadata
- Detail screen with read/edit switch
- Local persistence with Room
- Settings persistence with DataStore
- Chinese / English switching
- Custom cover image for each entry

## Tech Stack

- Kotlin
- Jetpack Compose
- Room
- DataStore
- Android Image Cropper

## Project Info

- Package: `com.example.myapplication`
- Min SDK: `24`
- Target SDK: `36`
- Version: `1.1`

## Run Locally

Open the project in Android Studio and run the `app` module on an emulator or Android device.

You can also build from terminal:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Build Release APK

This project already supports release builds. To generate the APK:

```powershell
.\gradlew.bat :app:assembleRelease
```

The generated file will be here:

```text
app/build/outputs/apk/release/app-release.apk
```

## How To Share The APK

The simplest way is to use **GitHub Releases** instead of committing the APK into the repository.

Recommended flow:

1. Push code to GitHub
2. Open the repository on GitHub
3. Create a new Release
4. Upload `app-release.apk` as a release asset
5. Share the Release page link with others

Why this is better:

- people can download directly in browser
- repository history stays clean
- APK files do not bloat git history
- you can keep multiple versions clearly

If you do not want to use Releases, a second simple option is to upload the APK to:

- Lanzou Cloud
- Google Drive
- OneDrive

But for an open-source project, **GitHub Releases** is the cleanest default.

## Status

Current version includes the main local diary flow, settings, persistence, theme customization, and APK release build support.

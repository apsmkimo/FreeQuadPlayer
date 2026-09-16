# TetraView - 4-Way Multi-Video Player

## Overview

TetraView is an open-source Android app for watching up to four local videos at once. Each launch you pick one of four layouts: a 2×2 landscape grid, a 1×4 portrait stack, a 1×2 portrait stack, or a 2×1 landscape pair. Independent Media3 ExoPlayer instances compare, mix, and review device footage—with simultaneous audio, per-cell controls, and strong support for legacy formats such as older AVI files.

## Key Features

- **Layout picker every launch** — A 2×2 glass tile grid: **2x2 Grid**, **1x4 Stack**, **1x2 Vertical**, **2x1 Horizontal**. The choice is in-session only (not stored). Cold start always shows the picker. Tap the layout button in the player (top-right) to switch without disposing players. The info button opens **About** (GPLv3, source URL, and open-source component licenses).
- **2x2 Grid** — Four independent local videos in a landscape 2×2 grid.
- **1x4 Stack** — Four players stacked top-to-bottom in portrait, hairline separators, FIT letterbox.
- **1x2 Vertical** — Two players stacked top-to-bottom in portrait.
- **2x1 Horizontal** — Two players side-by-side in landscape.
- **Intelligent Aspect-Ratio Matching** — Each cell uses FIT scaling so videos keep their native aspect ratio, with letterboxing or pillarboxing (black bars) when dimensions do not match the cell.
- **Folder-Grouped Video Picker** — Browse local videos by device directory (MediaStore buckets) with Coil thumbnails, file names, and durations instead of a flat system file browser.
- **Ultra-Thin Borders & Clean UI** — No title or action bar; cells maximize the viewing area with hairline separators only.
- **4-Way Independent Gesture Controls & Simultaneous Audio Mixing** — Per-cell overlay play/pause, seek, time labels, mute, and long-press volume gestures; all four players can output audio together.
- **Built-in High Fault-Tolerance for Legacy Formats** — FFmpeg-prefer software decoding for common old AVI codecs, audio-driven sync, skip/drop of late frames, and generous per-player buffering for smoother legacy playback.

## Tech Stack

| Layer | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Playback | Jetpack Media3 ExoPlayer |
| Decode | Media3 FFmpeg Extension (FFmpeg software decoding) |
| Thumbnails | Coil (`coil-compose`, `coil-video`) |
| CI/CD | GitHub Actions (`./gradlew assembleDebug`, artifact `app-debug`) |

**Package ID:** `com.apsmkimo.tetraview`  
**SDK:** minSdk 24 · compileSdk 36 · targetSdk 35  
**Version:** 1.0.12 (`versionCode` 112)

Official `androidx.media3:media3-decoder-ffmpeg` is not published on Maven Central. TetraView vendors the Media3 1.11.0 `decoder_ffmpeg` module with a prebuilt `libffmpegJNI.so` (FFmpeg 6.0). Modern H.264/HEVC streams stay on hardware MediaCodec; FFmpeg is preferred for allowlisted legacy codecs. See [decoder-ffmpeg/README.md](decoder-ffmpeg/README.md) for native rebuild notes.

## Open Source License

TetraView / FreeQuadPlayer is proudly licensed under the **GNU General Public License v3.0 (GPLv3)**.  
See the full text in [LICENSE](LICENSE).

## How to Download & Test

GitHub Actions builds a debug APK on every push to `main` and uploads it as an artifact. Public users can install it without building from source:

1. Open this repository on GitHub: [apsmkimo/FreeQuadPlayer](https://github.com/apsmkimo/FreeQuadPlayer).
2. Select the **Actions** tab.
3. In the left sidebar, open the **Android CI** workflow.
4. Choose the latest run with a green check mark (successful build on `main`).
5. Scroll to the **Artifacts** section at the bottom of the run page.
6. Download **`app-debug`** (GitHub delivers a zip file).
7. Unzip the archive to get `app-debug.apk`.
8. Copy the APK to an Android device or emulator (minSdk 24).
9. Allow installation from that source if prompted, then open the APK to install TetraView.
10. Grant video-read permission when asked (`READ_MEDIA_VIDEO` on Android 13+, otherwise `READ_EXTERNAL_STORAGE`).
11. Every launch, tap one of **2x2 Grid**, **1x4 Stack**, **1x2 Vertical**, or **2x1 Horizontal**. Tap an empty cell, pick a folder, then a video. Tap a playing cell to show overlay controls. Use the top-right layout button to switch modes later.

### Build from source (optional)

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

GitHub Actions does not require the Android NDK; FFmpeg JNI libraries are prebuilt and committed. Rebuilding natives needs NDK r26b and FFmpeg 6.0 (`./decoder-ffmpeg/rebuild-native.sh`).

## Debug signing (overwrite installs)

Local `./gradlew assembleDebug` and GitHub Actions CI use the same committed debug keystore so a new debug APK can overwrite an older TetraView debug install without uninstalling:

- Keystore: [`app/debug.keystore`](app/debug.keystore)
- Alias: `androiddebugkey`
- Store / key password: `android`

`app/build.gradle.kts` `signingConfigs.debug` points at that file. `*.keystore` stays in `.gitignore` except this one (`!app/debug.keystore`). If you still see a signing mismatch, the device has an older APK signed with a different key—uninstall once, then future upgrades from this keystore will succeed. `versionCode` must also increase (1.0.12 → 112).

# TetraView - 4-Way Multi-Video Player

## Overview

TetraView is an open-source Android app for watching up to four local videos at once. It places four independent Media3 ExoPlayer instances in a locked landscape 2×2 grid so you can compare, mix, and review device footage side by side—with simultaneous audio, per-cell controls, and strong support for legacy formats such as older AVI files.

## Key Features

- **2x2 Multi-Screen Grid Playback** — Stream four independent local videos simultaneously in a single landscape screen.
- **Locked Landscape Layout** — The activity stays in landscape only, optimized for widescreen viewing.
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

**Package ID:** `com.example.quadvideoplayer`  
**SDK:** minSdk 24 · compileSdk 36 · targetSdk 35  
**Version:** 1.0.1 (`versionCode` 101)

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
11. Tap an empty cell, pick a folder, then a video. Tap a playing cell to show its overlay controls.

### Build from source (optional)

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

GitHub Actions does not require the Android NDK; FFmpeg JNI libraries are prebuilt and committed. Rebuilding natives needs NDK r26b and FFmpeg 6.0 (`./decoder-ffmpeg/rebuild-native.sh`).

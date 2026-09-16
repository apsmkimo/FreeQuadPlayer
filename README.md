# TetraView

TetraView is a four-way local video player for Android. It puts four independent Media3 players on one landscape screen so you can compare, mix, and watch multiple device videos at the same time.

## Key Features

- **2x2 multi-screen grid** — four independent cells fill the display with hairline separators
- **Locked landscape** — the activity stays in landscape only
- **Aspect-ratio FIT scaling** — each video keeps its aspect ratio; unused cell area is letterboxed or pillarboxed in black
- **Folder-grouped file picking** — browse device folders (MediaStore buckets), then pick a video with Coil thumbnails, file name, and duration
- **Ultra-thin borders** — 1 dp hairlines only, no extra padding around the grid
- **Simultaneous 4-way audio mixing** — all four ExoPlayers can output audio at once (no exclusive mute)
- **Per-cell overlay controls** — tap a cell to show or hide play/pause, seek, time labels, and volume
- **Seek + volume gestures** — total duration sits before the slider and elapsed time after it; tap the speaker to mute/unmute, swipe vertically on the icon to change that cell’s volume
- **FFmpeg-prefer decode** — all four players use Media3 `EXTENSION_RENDERER_MODE_PREFER`. Bundled FFmpeg software decoders handle old-AVI **video** (MPEG-4 ASP / Xvid / DivX, MSMPEG4, MJPEG, H.263, MPEG-1/2, FLV, VC-1) and typical AVI **audio** (MP3/AC3/…). Modern H.264/HEVC MP4 stays on hardware MediaCodec; hardware remains the fallback when FFmpeg cannot handle a stream
- **AVI fault-tolerance** — audio-driven `DefaultMediaClock`, skip/drop late video instead of stalling, and a per-player `DefaultLoadControl` (5s start / 7s after rebuffer, time over size)

## Tech Stack

| Layer | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Playback | Jetpack Media3 ExoPlayer |
| Decode | Media3 FFmpeg Extension (FFmpeg software audio **and** video decoding) |
| Thumbnails | Coil (`coil-compose`, `coil-video`) |
| CI | GitHub Actions (`./gradlew assembleDebug`, artifact `app-debug`) |

Package ID: `com.example.quadvideoplayer`  
minSdk 24 · compileSdk 36 · targetSdk 35 · versionName 1.0.1

Official `androidx.media3:media3-decoder-ffmpeg` is not published on Maven. TetraView vendors the Media3 1.11.0 `decoder_ffmpeg` module, the working video path from [androidx/media PR 1591](https://github.com/androidx/media/pull/1591), and a prebuilt `libffmpegJNI.so` (FFmpeg 6.0 + libyuv). H.264/HEVC are not advertised so four-way modern MP4 keeps hardware decode. See [decoder-ffmpeg/README.md](decoder-ffmpeg/README.md).

Each of the four players uses its own `DefaultLoadControl` and `DefaultRenderersFactory` (Media3 1.11):

- Clock: `DefaultMediaClock` follows the audio renderer `MediaClock` (`FfmpegAudioRenderer` / `MediaCodecAudioRenderer`). Video does not own the clock.
- Drop late video: `DefaultRenderersFactory.experimentalSetLateThresholdToDropDecoderInputUs(5_000)` (5 ms, vs the 15 ms default) plus `setAllowedVideoJoiningTimeMs(15_000)` so joining does not freeze audio. `ExoPlayer.Builder.setVideoChangeFrameRateStrategy(C.VIDEO_CHANGE_FRAME_RATE_STRATEGY_OFF)` avoids display-refresh stalls.
- Buffers: `DefaultLoadControl.Builder.setBufferDurationsMs(15000, 50000, 5000, 7000)` and `setPrioritizeTimeOverSizeThresholds(true)`.
- Tunneling is **off**. Media3 tunneling ties MediaCodec video to one `AudioTrack` session and conflicts with four Compose `PlayerView` surfaces.

## How to download and test the compiled APK

GitHub Actions builds a debug APK on every push to `main` and uploads it as an artifact.

1. Open the repository on GitHub and select the **Actions** tab.
2. In the left sidebar, select the **Android CI** workflow.
3. Open the latest run with a green check mark (successful build on `main`).
4. Scroll to the **Artifacts** section at the bottom of the run page.
5. Download **`app-debug`**. GitHub delivers it as a zip file.
6. Unzip the archive to get `app-debug.apk`.
7. Copy the APK to an Android device (minSdk 24) or an emulator.
8. On the device, allow installation from this source if prompted, then open the APK to install TetraView.
9. Grant video-read permission when asked (`READ_MEDIA_VIDEO` on Android 13+, otherwise `READ_EXTERNAL_STORAGE`).
10. Tap an empty cell, choose a folder, then a video. Tap a playing cell to show its overlay; use the speaker icon (tap to mute, swipe vertically to change volume) and the seek bar.

You can also build locally:

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

GitHub Actions does not need the Android NDK: FFmpeg JNI libraries are prebuilt and committed. Rebuilding them requires NDK r26b and FFmpeg 6.0 (`./decoder-ffmpeg/rebuild-native.sh`).

## License

TetraView / FreeQuadPlayer is licensed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE).

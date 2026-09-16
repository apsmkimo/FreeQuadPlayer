# Media3 FFmpeg decoder (local module)

Official `androidx.media3:media3-decoder-ffmpeg` is **not** published to Google Maven
or Maven Central. This module vendors the Media3 **1.11.0** `decoder_ffmpeg` Java/JNI
sources from [androidx/media @ 1.11.0](https://github.com/androidx/media/tree/1.11.0/libraries/decoder_ffmpeg)
and links a GPLv3-compatible FFmpeg 6.0 native build so
`DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER` can actually load
`FfmpegAudioRenderer` (and the experimental video class) at runtime.

## What is shipped

- Java: `FfmpegAudioRenderer`, `FfmpegAudioDecoder`, `FfmpegLibrary`,
  `ExperimentalFfmpegVideoRenderer` (Media3 1.11.0; the video class is still a stub
  in upstream and always reports unsupported — hardware `MediaCodec` remains the
  video path, with decoder fallback).
- Native: `libffmpegJNI.so` for `armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`,
  built from FFmpeg **6.0** (`release/6.0`) with the official Media3
  `build_ffmpeg.sh` + `ffmpeg_jni.cc`.
- Enabled FFmpeg **audio** decoders (names used by `FfmpegLibrary.getCodecName`):
  `vorbis`, `opus`, `flac`, `alac`, `pcm_mulaw`, `pcm_alaw`, `mp3`, `aac`,
  `ac3`, `eac3`, `dca`, `mlp`, `truehd`, `amrnb`, `amrwb`.

`assembleDebug` / GitHub Actions do **not** need the NDK: prebuilt `.so` files
live under `src/main/jniLibs/`.

## Rebuild native libraries (optional)

Requires NDK r26b (`26.1.10909125`), CMake 3.21+, and network to clone FFmpeg:

```bash
./decoder-ffmpeg/rebuild-native.sh
```

The FFmpeg source tree under `src/main/jni/ffmpeg/` is gitignored and is not
required to compile the app.

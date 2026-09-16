# Media3 FFmpeg decoder (local module)

Official `androidx.media3:media3-decoder-ffmpeg` is **not** published to Google Maven
or Maven Central. This module vendors the Media3 **1.11.0** `decoder_ffmpeg` Java/JNI
sources from [androidx/media @ 1.11.0](https://github.com/androidx/media/tree/1.11.0/libraries/decoder_ffmpeg)
plus the working video path from [androidx/media PR 1591](https://github.com/androidx/media/pull/1591)
(GPLv3-compatible FFmpeg 6.0 + libyuv, statically linked into `libffmpegJNI.so`).

`DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER` then loads:

- `FfmpegAudioRenderer` for audio the extension supports
- `ExperimentalFfmpegVideoRenderer` for **old-AVI video codecs only**

## Codec allowlist (video)

FFmpeg is preferred for:

| MIME (Media3) | FFmpeg decoder |
| --- | --- |
| `video/mp4v-es`, `video/divx` | `mpeg4` (Xvid / DivX / MPEG-4 ASP) |
| `video/mp42` | `msmpeg4v2` |
| `video/mp43` | `msmpeg4v3` |
| `video/mpeg`, `video/mpeg2` | `mpeg2video` |
| `video/mjpeg` | `mjpeg` |
| `video/3gpp` | `h263` |
| `video/x-flv` | `flv` |
| `video/wvc1` | `vc1` |

**Not** advertised: H.264, HEVC, VP8, VP9, AV1. Those stay on hardware
`MediaCodec` so four-way modern MP4 does not run four software decoders.

Audio still includes MP3 / AC3 / AAC / FLAC / Opus / … (AVI-typical MP3/AC3).

## What is shipped

- Java: `FfmpegAudioRenderer`, `ExperimentalFfmpegVideoRenderer` +
  `ExperimentalFfmpegVideoDecoder`
- Native: one `libffmpegJNI.so` per ABI (`armeabi-v7a`, `arm64-v8a`, `x86`,
  `x86_64`) containing FFmpeg 6.0 (`libavcodec` / `libswscale` / `libswresample` /
  `libavutil`) and libyuv (I420 rotate + `ANativeWindow` YV12 blit)
- Video threads capped at 2 per player to limit 4-way CPU load

`assembleDebug` / GitHub Actions do **not** need the NDK.

## Rebuild native libraries (optional)

Requires NDK r26b (`26.1.10909125`), CMake 3.21+, and network:

```bash
./decoder-ffmpeg/rebuild-native.sh
```

## Residual limitations

- Upstream SimpleDecoder + FFmpeg B-frames: last few frames of B-frame streams
  may be dropped at EOS (typical MPEG-4 ASP AVI is I/P and is fine).
- Packed MPEG-4 AVI (`mpeg4_unpack_bframes`) may still need extractor/BSF help.
- Four simultaneous FFmpeg video soft-decodes is CPU-heavy on low-end devices.

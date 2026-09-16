#!/usr/bin/env bash
# Rebuild libffmpegJNI.so (audio + video) from Media3 JNI + FFmpeg 6.0 + libyuv.
# Not required for ./gradlew assembleDebug (prebuilt jniLibs are committed).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
FFMPEG_MODULE_PATH="${ROOT}/src/main"
NDK_PATH="${ANDROID_NDK_HOME:-${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}/ndk/26.1.10909125}"
HOST_PLATFORM="${HOST_PLATFORM:-linux-x86_64}"
ANDROID_ABI="${ANDROID_ABI:-21}"
CMAKE_BIN="${CMAKE_BIN:-$(command -v cmake || true)}"
# Audio (FfmpegAudioRenderer) + old-AVI video (ExperimentalFfmpegVideoRenderer).
# H.264 / HEVC / AV1 are intentionally omitted so PREFER leaves modern MP4 on MediaCodec.
ENABLED_DECODERS=(
  vorbis opus flac alac pcm_mulaw pcm_alaw mp3 aac ac3 eac3 dca mlp truehd amrnb amrwb
  mpeg4 msmpeg4v1 msmpeg4v2 msmpeg4v3 mjpeg h263 mpeg1video mpeg2video flv wmv1 wmv2 vc1
  msvideo1 cinepak
)

if [[ ! -d "${NDK_PATH}" ]]; then
  echo "NDK not found at ${NDK_PATH}. Install ndk;26.1.10909125 or set ANDROID_NDK_HOME." >&2
  exit 1
fi

if [[ -z "${CMAKE_BIN}" || ! -x "${CMAKE_BIN}" ]]; then
  CMAKE_BIN="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}/cmake/3.22.1/bin/cmake"
fi
NINJA="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}/cmake/3.22.1/bin/ninja"

if [[ ! -x "${FFMPEG_MODULE_PATH}/jni/ffmpeg/configure" ]]; then
  git clone --branch release/6.0 --depth 1 https://github.com/FFmpeg/FFmpeg.git \
    "${FFMPEG_MODULE_PATH}/jni/ffmpeg"
fi

if [[ ! -f "${FFMPEG_MODULE_PATH}/jni/libyuv/CMakeLists.txt" ]]; then
  git clone --depth 1 https://chromium.googlesource.com/libyuv/libyuv \
    "${FFMPEG_MODULE_PATH}/jni/libyuv"
fi

"${FFMPEG_MODULE_PATH}/jni/build_ffmpeg.sh" \
  "${FFMPEG_MODULE_PATH}" \
  "${NDK_PATH}" \
  "${HOST_PLATFORM}" \
  "${ANDROID_ABI}" \
  "${ENABLED_DECODERS[@]}"

TOOLCHAIN="${NDK_PATH}/build/cmake/android.toolchain.cmake"
for ABI in armeabi-v7a arm64-v8a x86 x86_64; do
  YUV_BUILD="${FFMPEG_MODULE_PATH}/jni/libyuv/build-${ABI}"
  YUV_OUT="${FFMPEG_MODULE_PATH}/jni/libyuv/android-libs/${ABI}"
  rm -rf "${YUV_BUILD}"
  mkdir -p "${YUV_BUILD}" "${YUV_OUT}"
  PLATFORM="${ANDROID_ABI}"
  if [[ "${ABI}" == "arm64-v8a" || "${ABI}" == "x86_64" ]]; then
    if [[ "${PLATFORM}" -lt 21 ]]; then
      PLATFORM=21
    fi
  fi
  "${CMAKE_BIN}" -S "${FFMPEG_MODULE_PATH}/jni/libyuv" -B "${YUV_BUILD}" \
    -G Ninja \
    -DCMAKE_MAKE_PROGRAM="${NINJA}" \
    -DCMAKE_TOOLCHAIN_FILE="${TOOLCHAIN}" \
    -DANDROID_ABI="${ABI}" \
    -DANDROID_PLATFORM="android-${PLATFORM}" \
    -DANDROID_NDK="${NDK_PATH}" \
    -DCMAKE_BUILD_TYPE=Release \
    -DANDROID_STL=c++_static \
    -DBUILD_SHARED_LIBS=OFF
  "${CMAKE_BIN}" --build "${YUV_BUILD}" --target yuv
  YUV_A="$(find "${YUV_BUILD}" -name 'libyuv.a' | head -n 1)"
  if [[ -z "${YUV_A}" ]]; then
    echo "libyuv.a not produced for ${ABI}" >&2
    exit 1
  fi
  cp -f "${YUV_A}" "${YUV_OUT}/libyuv.a"
done

for ABI in armeabi-v7a arm64-v8a x86 x86_64; do
  BUILD_DIR="${ROOT}/.cxx-rebuild/${ABI}"
  OUT_DIR="${ROOT}/src/main/jniLibs/${ABI}"
  mkdir -p "${BUILD_DIR}" "${OUT_DIR}"
  "${CMAKE_BIN}" -S "${FFMPEG_MODULE_PATH}/jni" -B "${BUILD_DIR}" \
    -G Ninja \
    -DCMAKE_MAKE_PROGRAM="${NINJA}" \
    -DANDROID_ABI="${ABI}" \
    -DANDROID_PLATFORM="android-${ANDROID_ABI}" \
    -DANDROID_NDK="${NDK_PATH}" \
    -DCMAKE_TOOLCHAIN_FILE="${TOOLCHAIN}" \
    -DCMAKE_BUILD_TYPE=Release \
    -DANDROID_STL=c++_static
  "${CMAKE_BIN}" --build "${BUILD_DIR}" --target ffmpegJNI
  SO="$(find "${BUILD_DIR}" -name 'libffmpegJNI.so' | head -n 1)"
  if [[ -z "${SO}" ]]; then
    echo "libffmpegJNI.so not produced for ${ABI}" >&2
    exit 1
  fi
  STRIP="${NDK_PATH}/toolchains/llvm/prebuilt/${HOST_PLATFORM}/bin/llvm-strip"
  if [[ -x "${STRIP}" ]]; then
    "${STRIP}" --strip-unneeded "${SO}"
  fi
  cp -f "${SO}" "${OUT_DIR}/libffmpegJNI.so"
  echo "Installed ${OUT_DIR}/libffmpegJNI.so"
done

echo "Native rebuild complete."

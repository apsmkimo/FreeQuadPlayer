#!/usr/bin/env bash
# Rebuild libffmpegJNI.so for all ABIs from Media3 1.11.0 JNI + FFmpeg 6.0.
# Not required for ./gradlew assembleDebug (prebuilt jniLibs are committed).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
FFMPEG_MODULE_PATH="${ROOT}/src/main"
NDK_PATH="${ANDROID_NDK_HOME:-${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}/ndk/26.1.10909125}"
HOST_PLATFORM="${HOST_PLATFORM:-linux-x86_64}"
ANDROID_ABI="${ANDROID_ABI:-21}"
CMAKE_BIN="${CMAKE_BIN:-$(command -v cmake || true)}"
ENABLED_DECODERS=(vorbis opus flac alac pcm_mulaw pcm_alaw mp3 aac ac3 eac3 dca mlp truehd amrnb amrwb)

if [[ ! -d "${NDK_PATH}" ]]; then
  echo "NDK not found at ${NDK_PATH}. Install ndk;26.1.10909125 or set ANDROID_NDK_HOME." >&2
  exit 1
fi

if [[ ! -x "${FFMPEG_MODULE_PATH}/jni/ffmpeg/configure" ]]; then
  git clone --branch release/6.0 --depth 1 https://github.com/FFmpeg/FFmpeg.git \
    "${FFMPEG_MODULE_PATH}/jni/ffmpeg"
fi

"${FFMPEG_MODULE_PATH}/jni/build_ffmpeg.sh" \
  "${FFMPEG_MODULE_PATH}" \
  "${NDK_PATH}" \
  "${HOST_PLATFORM}" \
  "${ANDROID_ABI}" \
  "${ENABLED_DECODERS[@]}"

if [[ -z "${CMAKE_BIN}" ]]; then
  CMAKE_BIN="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}/cmake/3.22.1/bin/cmake"
fi

TOOLCHAIN="${NDK_PATH}/build/cmake/android.toolchain.cmake"
for ABI in armeabi-v7a arm64-v8a x86 x86_64; do
  BUILD_DIR="${ROOT}/.cxx-rebuild/${ABI}"
  OUT_DIR="${ROOT}/src/main/jniLibs/${ABI}"
  mkdir -p "${BUILD_DIR}" "${OUT_DIR}"
  "${CMAKE_BIN}" -S "${FFMPEG_MODULE_PATH}/jni" -B "${BUILD_DIR}" \
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

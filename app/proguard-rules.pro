# QuadVideoPlayer — debug/release without minify for the sample app.
# Keep Media3 / Compose defaults if minify is later enabled.

# SMCPKG_SUPPORT>>>Cursor009
# Media3 FFmpeg extension (loaded by DefaultRenderersFactory via reflection).
-keep class androidx.media3.decoder.ffmpeg.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
# SMCPKG_SUPPORT<<<Cursor009

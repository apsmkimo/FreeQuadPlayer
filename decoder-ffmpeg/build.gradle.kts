// Copyright (C) 2016 The Android Open Source Project
// Vendored from androidx/media 1.11.0 libraries/decoder_ffmpeg and adapted
// to resolve Media3 artifacts from Maven instead of the Media3 composite build.
//
// Official artifact androidx.media3:media3-decoder-ffmpeg is NOT published
// to Google Maven / Maven Central. This local module is the supported way
// to compile and link FFmpeg software decoders against Media3 1.11 APIs.

plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "androidx.media3.decoder.ffmpeg"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("proguard-rules.txt")
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Prebuilt libffmpegJNI.so in jniLibs keeps GitHub Actions (no NDK) working.
    // Rebuild natives with ./decoder-ffmpeg/rebuild-native.sh (NDK + FFmpeg 6.0).
    sourceSets {
        getByName("main") {
            jniLibs.srcDir("src/main/jniLibs")
        }
    }
}

dependencies {
    api(libs.androidx.media3.decoder)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.common)
    compileOnly(libs.checker.qual)
    implementation(libs.androidx.annotation)
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// SMCPKG_SUPPORT>>>Cursor009
/** Current release name. Next release: increment the third component by 1 (1.0.0 → 1.0.1). */
// SMCPKG_SUPPORT>>>Cursor011
// val APP_VERSION_NAME = "1.0.0"
// val APP_VERSION_NAME = "1.0.1"
// val APP_VERSION_NAME = "1.0.2"
// val APP_VERSION_NAME = "1.0.3"
val APP_VERSION_NAME = "1.0.4"
// SMCPKG_SUPPORT<<<Cursor011

/**
 * Maps versionName to versionCode: 1.0.0 → 100 … 1.0.4 → 104.
 * Increment versionCode by 1 whenever versionName increases by 0.01.
 */
fun versionCodeFor(versionName: String): Int {
    val parts = versionName.split(".")
    require(parts.size == 3) { "versionName must be major.minor.patch, e.g. 1.0.0" }
    val major = parts[0].toInt()
    val minor = parts[1].toInt()
    val patch = parts[2].toInt()
    return major * 100 + minor * 0 + patch
}
// SMCPKG_SUPPORT<<<Cursor009

android {
    namespace = "com.example.quadvideoplayer"
    // SMCPKG_SUPPORT>>>Cursor001
    // compileSdk = 35
    compileSdk = 36
    // SMCPKG_SUPPORT<<<Cursor001

    defaultConfig {
        applicationId = "com.example.quadvideoplayer"
        minSdk = 24
        targetSdk = 35
        // SMCPKG_SUPPORT>>>Cursor009
        // versionCode = 1
        // versionName = "1.0"
        //
        // Release versioning:
        // - versionName starts at "1.0.0".
        // - Each subsequent release increments versionName by 0.01
        //   (next = 1.0.1, then 1.0.2, …).
        // - versionCode is a simple integer: 100 for 1.0.0, then +1 per release
        //   (1.0.1 → 101, 1.0.2 → 102). Helper below keeps them in sync.
        versionName = APP_VERSION_NAME
        versionCode = versionCodeFor(APP_VERSION_NAME)
        // SMCPKG_SUPPORT<<<Cursor009
    }

    // SMCPKG_SUPPORT>>>Cursor012
    // Debug APKs (local + GitHub Actions assembleDebug) always use the committed
    // project keystore at app/debug.keystore so overwrite-install upgrades work.
    // Store password / key password / alias match the standard Android debug key.
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    // SMCPKG_SUPPORT<<<Cursor012

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // SMCPKG_SUPPORT>>>Cursor003
    // kotlinOptions {
    //     jvmTarget = "17"
    // }
    // SMCPKG_SUPPORT<<<Cursor003

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        // SMCPKG_SUPPORT>>>Cursor009
        jniLibs {
            // Ship uncompressed libffmpegJNI.so from :decoder-ffmpeg.
            useLegacyPackaging = false
        }
        // SMCPKG_SUPPORT<<<Cursor009
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
    // SMCPKG_SUPPORT>>>Cursor009
    // Official androidx.media3:media3-decoder-ffmpeg:1.11.0 is not on Maven.
    // Local module vendors Media3 1.11.0 decoder_ffmpeg + prebuilt FFmpeg JNI.
    implementation(project(":decoder-ffmpeg"))
    // SMCPKG_SUPPORT<<<Cursor009

    // SMCPKG_SUPPORT>>>Cursor005
    implementation(libs.coil.compose)
    implementation(libs.coil.video)
    // SMCPKG_SUPPORT<<<Cursor005
}

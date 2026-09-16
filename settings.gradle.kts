pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "QuadVideoPlayer"
include(":app")
// SMCPKG_SUPPORT>>>Cursor009
// Official androidx.media3:media3-decoder-ffmpeg is not on Maven; local 1.11.0 module.
include(":decoder-ffmpeg")
// SMCPKG_SUPPORT<<<Cursor009

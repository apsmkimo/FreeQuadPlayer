# QuadVideoPlayer

四格同步本機影片播放器 / Four-way synchronized local video player

- **Package:** `com.example.quadvideoplayer`
- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Media:** Jetpack Media3 ExoPlayer `1.11.0`

## 功能 Features

- 2×2 `LazyVerticalGrid` 同時播放 4 部獨立本機影片
- 預設僅 1 格有聲音（左上），點選某一格會把聲音切到該格，其餘三格靜音
- 全域主控按鈕一次播放 / 暫停四個播放器
- Android 13+ 使用 `READ_MEDIA_VIDEO`，較舊版本使用 `READ_EXTERNAL_STORAGE`
- 每個格子可用系統檔案選擇器（`OpenDocument` / `video/*`）挑選影片
- `DisposableEffect` 在離開畫面時釋放全部 4 個 ExoPlayer，避免洩漏

## 需求 Requirements

- Android Studio Hedgehog 或更新版本（建議使用最新穩定版）
- JDK 17
- Android SDK Platform 35
- 實體裝置或模擬器，minSdk 24

## 用 Android Studio 開啟與執行

### 中文

1. 安裝 [Android Studio](https://developer.android.com/studio) 並透過 SDK Manager 安裝 **Android SDK 35**、**Build-Tools**。
2. **File → Open**，選取本倉庫根目錄（含 `settings.gradle.kts` 的資料夾）。
3. 等待 Gradle Sync 完成。
4. 連接 Android 裝置（或啟動模擬器），裝置上請準備至少一支本機影片。
5. 點選 **Run**（綠色三角形）安裝 `app` 模組。
6. 首次啟動可授予「讀取影片」權限；也可以略過權限、直接點格子用系統選擇器挑檔。
7. 為四格各自選片後，按右上角 **全部播放**。點某一格可把聲音切到該格。

### English

1. Install [Android Studio](https://developer.android.com/studio) and the **Android 35** SDK platform.
2. **File → Open** the repository root (the folder that contains `settings.gradle.kts`).
3. Wait for Gradle Sync to finish.
4. Connect a device or start an emulator that already has local video files.
5. Run the `app` configuration.
6. Grant video-read permission, or skip it and pick files via the system document picker on each cell.
7. After assigning videos, tap **Play all**. Tap a cell to make that cell the only unmuted player.

## 專案結構 Project structure

```
QuadVideoPlayer/
├── app/
│   ├── build.gradle.kts
│   └── src/main/java/com/example/quadvideoplayer/
│       ├── MainActivity.kt
│       ├── player/QuadPlayerController.kt
│       ├── ui/QuadPlayerScreen.kt
│       ├── ui/VideoCell.kt
│       ├── ui/PermissionBanner.kt
│       ├── ui/theme/
│       └── util/VideoPermissions.kt
├── gradle/libs.versions.toml
├── settings.gradle.kts
└── .github/workflows/android.yml
```

## 指令列建置 Build from CLI

```bash
./gradlew assembleDebug
```

輸出 APK：`app/build/outputs/apk/debug/app-debug.apk`

推送到 `main` 時，GitHub Actions 會用 JDK 17 執行同樣的 debug 建置，並上傳 `app-debug` artifact。

## SDK / 依賴 Versions

| Item | Version |
| --- | --- |
| minSdk | 24 |
| compileSdk / targetSdk | 35 |
| AGP | 8.7.3 |
| Kotlin | 2.0.21 |
| Compose BOM | 2024.12.01 |
| Media3 | 1.11.0 |

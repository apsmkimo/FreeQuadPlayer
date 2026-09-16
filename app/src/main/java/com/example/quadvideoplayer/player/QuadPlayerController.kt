package com.example.quadvideoplayer.player

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.extractor.avi.AviExtractor
import androidx.media3.extractor.text.DefaultSubtitleParserFactory

/**
 * Holds four independent ExoPlayers. Each can play audio at the same time.
 */
@OptIn(UnstableApi::class)
class QuadPlayerController(
    context: Context,
    val playerCount: Int = PLAYER_COUNT,
) {
    private val appContext = context.applicationContext
    private val dataSourceFactory = DefaultDataSource.Factory(appContext)
    private val defaultMediaSourceFactory = DefaultMediaSourceFactory(appContext)
    private val aviMediaSourceFactory = ProgressiveMediaSource.Factory(
        dataSourceFactory,
        ExtractorsFactory { arrayOf(AviExtractor(0, DefaultSubtitleParserFactory())) },
    )

    val players: List<ExoPlayer> = List(playerCount) {
        // SMCPKG_SUPPORT>>>Cursor007
        // ExoPlayer.Builder(context.applicationContext, createSoftDecodeRenderersFactory(context))
        //     .build()
        // SMCPKG_SUPPORT>>>Cursor008
        createHardwareFirstPlayer(appContext)
            // SMCPKG_SUPPORT<<<Cursor008
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                // SMCPKG_SUPPORT>>>Cursor004
                // volume = if (index == DEFAULT_UNMUTED_INDEX) 1f else 0f
                volume = 1f
                setAudioAttributes(concurrentMediaAttributes(), /* handleAudioFocus = */ false)
                // SMCPKG_SUPPORT<<<Cursor004
                playWhenReady = false
            }
        // SMCPKG_SUPPORT<<<Cursor007
    }

    @Volatile
    private var released: Boolean = false

    // SMCPKG_SUPPORT>>>Cursor004
    // fun setUnmuted(index: Int) {
    //     if (released) return
    //     players.forEachIndexed { i, player ->
    //         player.volume = if (i == index) 1f else 0f
    //     }
    // }
    //
    // fun setPlaying(playing: Boolean) {
    //     if (released) return
    //     players.forEach { player ->
    //         player.playWhenReady = playing
    //     }
    // }
    // SMCPKG_SUPPORT<<<Cursor004

    fun pauseAll() {
        if (released) return
        players.forEach { it.pause() }
    }

    fun resumePlaying(playingFlags: List<Boolean>) {
        if (released) return
        players.forEachIndexed { index, player ->
            if (playingFlags.getOrElse(index) { false }) {
                player.play()
            }
        }
    }

    fun snapshotPlaying(): List<Boolean> {
        if (released) return List(playerCount) { false }
        return players.map { it.playWhenReady || it.isPlaying }
    }

    @OptIn(UnstableApi::class)
    fun setVideo(index: Int, uri: Uri?, playWhenReady: Boolean) {
        if (released) return
        val player = players[index]
        if (uri == null) {
            player.stop()
            player.clearMediaItems()
            return
        }
        // SMCPKG_SUPPORT>>>Cursor008
        // player.setMediaItem(MediaItem.fromUri(uri))
        player.setMediaSource(createMediaSource(uri))
        // SMCPKG_SUPPORT<<<Cursor008
        player.prepare()
        player.playWhenReady = playWhenReady
        player.volume = 1f
        player.setAudioAttributes(concurrentMediaAttributes(), /* handleAudioFocus = */ false)
    }

    private fun createMediaSource(uri: Uri): MediaSource {
        val mediaItem = MediaItem.fromUri(uri)
        return if (isAviUri(uri)) {
            aviMediaSourceFactory.createMediaSource(mediaItem)
        } else {
            defaultMediaSourceFactory.createMediaSource(mediaItem)
        }
    }

    private fun isAviUri(uri: Uri): Boolean {
        val pathHint = listOfNotNull(uri.lastPathSegment, uri.path, uri.toString())
            .joinToString(" ")
        if (pathHint.contains(".avi", ignoreCase = true)) {
            return true
        }
        val mime = appContext.contentResolver.getType(uri)?.lowercase().orEmpty()
        if (mime in AVI_MIME_TYPES) {
            return true
        }
        return appContext.contentResolver.query(
            uri,
            arrayOf(
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.MIME_TYPE,
            ),
            null,
            null,
            null,
        )?.use { cursor ->
            if (!cursor.moveToFirst()) {
                return@use false
            }
            val name = cursor.getString(0).orEmpty()
            val type = cursor.getString(1).orEmpty().lowercase()
            name.endsWith(".avi", ignoreCase = true) || type in AVI_MIME_TYPES
        } ?: false
    }

    fun releaseAll() {
        if (released) return
        released = true
        players.forEach { player ->
            player.playWhenReady = false
            player.stop()
            player.clearMediaItems()
            player.release()
        }
    }

    companion object {
        const val PLAYER_COUNT = 4
        // SMCPKG_SUPPORT>>>Cursor004
        // const val DEFAULT_UNMUTED_INDEX = 0
        // SMCPKG_SUPPORT<<<Cursor004
        const val GRID_COLUMNS = 2

        // Larger min/max than the 2.5s/5s start thresholds so AVI can preload,
        // without exceeding 50s cached per player (4-way memory budget).
        const val MIN_BUFFER_MS = 15_000
        const val MAX_BUFFER_MS = 50_000
        const val BUFFER_FOR_PLAYBACK_MS = 2_500
        const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5_000

        private val AVI_MIME_TYPES = setOf(
            "video/avi",
            "video/x-msvideo",
            "video/vnd.avi",
            "video/msvideo",
        )

        // SMCPKG_SUPPORT>>>Cursor007
        // fun createSoftDecodeRenderersFactory(context: Context): DefaultRenderersFactory {
        //     return DefaultRenderersFactory(context.applicationContext)
        //         .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        //         .setEnableDecoderFallback(true)
        //         .setMediaCodecSelector(preferSoftwareMediaCodecSelector())
        // }
        //
        // fun preferSoftwareMediaCodecSelector(): MediaCodecSelector {
        //     return MediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
        //         MediaCodecUtil.getDecoderInfos(...).sortedBy { if (it.hardwareAccelerated) 1 else 0 }
        //     }
        // }
        // SMCPKG_SUPPORT<<<Cursor007

        @OptIn(UnstableApi::class)
        fun createHardwareFirstPlayer(context: Context): ExoPlayer {
            val appContext = context.applicationContext
            val renderersFactory = DefaultRenderersFactory(appContext)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                .setEnableDecoderFallback(true)
                .setEnableAudioTrackPlaybackParams(true)
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    MIN_BUFFER_MS,
                    MAX_BUFFER_MS,
                    BUFFER_FOR_PLAYBACK_MS,
                    BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
                )
                .build()
            return ExoPlayer.Builder(appContext, renderersFactory)
                .setLoadControl(loadControl)
                .setVideoChangeFrameRateStrategy(C.VIDEO_CHANGE_FRAME_RATE_STRATEGY_OFF)
                .build()
        }

        fun concurrentMediaAttributes(): AudioAttributes {
            return AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                // SMCPKG_SUPPORT>>>Cursor004
                // .setContentType(C.CONTENT_TYPE_MOVIE)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                // SMCPKG_SUPPORT<<<Cursor004
                .build()
        }
    }
}

package com.example.quadvideoplayer.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil

/**
 * Holds four independent ExoPlayers. Each can play audio at the same time.
 */
@OptIn(UnstableApi::class)
class QuadPlayerController(
    context: Context,
    val playerCount: Int = PLAYER_COUNT,
) {
    val players: List<ExoPlayer> = List(playerCount) {
        // SMCPKG_SUPPORT>>>Cursor007
        // ExoPlayer.Builder(context.applicationContext).build().apply {
        ExoPlayer.Builder(context.applicationContext, createSoftDecodeRenderersFactory(context))
            .build()
            .apply {
        // SMCPKG_SUPPORT<<<Cursor007
            repeatMode = Player.REPEAT_MODE_ONE
            // SMCPKG_SUPPORT>>>Cursor004
            // volume = if (index == DEFAULT_UNMUTED_INDEX) 1f else 0f
            volume = 1f
            setAudioAttributes(concurrentMediaAttributes(), /* handleAudioFocus = */ false)
            // SMCPKG_SUPPORT<<<Cursor004
            playWhenReady = false
        }
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
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.playWhenReady = playWhenReady
        player.volume = 1f
        player.setAudioAttributes(concurrentMediaAttributes(), /* handleAudioFocus = */ false)
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

        @OptIn(UnstableApi::class)
        fun createSoftDecodeRenderersFactory(context: Context): DefaultRenderersFactory {
            return DefaultRenderersFactory(context.applicationContext)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                .setEnableDecoderFallback(true)
                .setMediaCodecSelector(preferSoftwareMediaCodecSelector())
        }

        @OptIn(UnstableApi::class)
        fun preferSoftwareMediaCodecSelector(): MediaCodecSelector {
            return MediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
                MediaCodecUtil.getDecoderInfos(
                    mimeType,
                    requiresSecureDecoder,
                    requiresTunnelingDecoder,
                ).sortedBy { info -> if (info.hardwareAccelerated) 1 else 0 }
            }
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

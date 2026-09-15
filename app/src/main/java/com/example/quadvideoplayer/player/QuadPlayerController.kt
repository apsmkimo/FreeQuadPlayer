package com.example.quadvideoplayer.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

/**
 * Holds four independent ExoPlayers plus the shared mute / play-pause policy.
 */
class QuadPlayerController(
    context: Context,
    val playerCount: Int = PLAYER_COUNT,
) {
    val players: List<ExoPlayer> = List(playerCount) { index ->
        ExoPlayer.Builder(context.applicationContext).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            volume = if (index == DEFAULT_UNMUTED_INDEX) 1f else 0f
            playWhenReady = false
        }
    }

    @Volatile
    private var released: Boolean = false

    fun setUnmuted(index: Int) {
        if (released) return
        players.forEachIndexed { i, player ->
            player.volume = if (i == index) 1f else 0f
        }
    }

    fun setPlaying(playing: Boolean) {
        if (released) return
        players.forEach { player ->
            player.playWhenReady = playing
        }
    }

    fun pauseAll() {
        if (released) return
        players.forEach { it.pause() }
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
        const val DEFAULT_UNMUTED_INDEX = 0
        const val GRID_COLUMNS = 2
    }
}

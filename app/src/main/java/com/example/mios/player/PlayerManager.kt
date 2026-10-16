package com.example.mios.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerManager(private val context: Context) {

    private var controller: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _isRepeatOneEnabled = MutableStateFlow(false)
    val isRepeatOneEnabled: StateFlow<Boolean> = _isRepeatOneEnabled

    private val _isRepeatAllEnabled = MutableStateFlow(false)
    val isRepeatAllEnabled: StateFlow<Boolean> = _isRepeatAllEnabled

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _hasNext = MutableStateFlow(false)
    val hasNext: StateFlow<Boolean> = _hasNext

    private val _hasPrevious = MutableStateFlow(false)
    val hasPrevious: StateFlow<Boolean> = _hasPrevious

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _duration.value = controller?.duration?.coerceAtLeast(0L) ?: 0L
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateIndexAndNav()
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            updateIndexAndNav()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _isShuffleEnabled.value = shuffleModeEnabled
            updateIndexAndNav()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _isRepeatOneEnabled.value = repeatMode == Player.REPEAT_MODE_ONE
            _isRepeatAllEnabled.value = repeatMode == Player.REPEAT_MODE_ALL
            updateIndexAndNav()
        }
    }

    private fun updateIndexAndNav() {
        controller?.let {
            _currentIndex.value = it.currentMediaItemIndex
            _hasNext.value = it.hasNextMediaItem()
            _hasPrevious.value = it.hasPreviousMediaItem()
        }
    }

    fun connect(onConnected: () -> Unit) {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller?.addListener(playerListener)
            onConnected()
        }, MoreExecutors.directExecutor())
    }

    fun playQueue(mediaStoreIds: List<Long>, startIndex: Int = 0) {
        val mediaItems = mediaStoreIds.map { id ->
            MediaItem.Builder()
                .setMediaId(id.toString())
                .setUri("content://media/external/audio/media/$id")
                .build()
        }
        controller?.setMediaItems(mediaItems, startIndex, 0L)
        controller?.prepare()
        controller?.play()
    }

    fun removeItemAt(index: Int) {
        controller?.removeMediaItem(index)
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun next() {
        controller?.seekToNextMediaItem()
    }

    fun previous() {
        controller?.seekToPreviousMediaItem()
    }

    fun toggleRepeatOne() {
        controller?.let {
            val newMode = if (it.repeatMode == Player.REPEAT_MODE_ONE) Player.REPEAT_MODE_OFF else Player.REPEAT_MODE_ONE
            it.repeatMode = newMode
            _isRepeatOneEnabled.value = newMode == Player.REPEAT_MODE_ONE
            _isRepeatAllEnabled.value = false
        }
    }

    fun toggleRepeatAll() {
        controller?.let {
            val newMode = if (it.repeatMode == Player.REPEAT_MODE_ALL) Player.REPEAT_MODE_OFF else Player.REPEAT_MODE_ALL
            it.repeatMode = newMode
            _isRepeatAllEnabled.value = newMode == Player.REPEAT_MODE_ALL
            _isRepeatOneEnabled.value = false
        }
    }

    fun toggleShuffle() {
        controller?.let {
            it.shuffleModeEnabled = !it.shuffleModeEnabled
            _isShuffleEnabled.value = it.shuffleModeEnabled
        }
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    fun updatePosition() {
        _currentPosition.value = controller?.currentPosition?.coerceAtLeast(0L) ?: 0L
        _duration.value = controller?.duration?.coerceAtLeast(0L) ?: 0L
    }

    fun release() {
        controller?.release()
    }
}
package com.example.mios.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mios.data.database.Song
import com.example.mios.data.repository.SongRepository
import com.example.mios.player.PlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SongRepository(application)
    private val playerManager = PlayerManager(application)

    val songs: StateFlow<List<Song>> = repository.getCatalogedSongs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _queue = MutableStateFlow<List<Song>>(emptyList())

    private val _currentPlaylistId = MutableStateFlow<Long?>(null)
    val currentPlaylistId: StateFlow<Long?> = _currentPlaylistId

    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying
    val currentPosition: StateFlow<Long> = playerManager.currentPosition
    val duration: StateFlow<Long> = playerManager.duration
    val isRepeatOneEnabled: StateFlow<Boolean> = playerManager.isRepeatOneEnabled
    val isRepeatAllEnabled: StateFlow<Boolean> = playerManager.isRepeatAllEnabled
    val hasNext: StateFlow<Boolean> = playerManager.hasNext
    val hasPrevious: StateFlow<Boolean> = playerManager.hasPrevious

    val currentSong: StateFlow<Song?> = combine(_queue, playerManager.currentIndex) { queue, index ->
        queue.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        playerManager.connect {
            viewModelScope.launch {
                while (true) {
                    playerManager.updatePosition()
                    delay(500)
                }
            }
        }
    }

    fun playSong(song: Song) {
        _queue.value = listOf(song)
        _currentPlaylistId.value = null
        playerManager.playQueue(listOf(song.mediaStoreId), 0)
    }

    fun playPlaylist(songsList: List<Song>, startIndex: Int, playlistId: Long) {
        if (songsList.isEmpty()) return
        _queue.value = songsList
        _currentPlaylistId.value = playlistId
        playerManager.playQueue(songsList.map { it.mediaStoreId }, startIndex)
    }

    fun playNext() = playerManager.next()
    fun playPrevious() = playerManager.previous()
    fun togglePlayPause() = playerManager.togglePlayPause()
    fun toggleRepeatOne() = playerManager.toggleRepeatOne()
    fun toggleRepeatAll() = playerManager.toggleRepeatAll()
    fun seekTo(positionMs: Long) = playerManager.seekTo(positionMs)

    fun deleteSongFromLibrary(song: Song) {
        viewModelScope.launch {
            repository.deleteSong(song)
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
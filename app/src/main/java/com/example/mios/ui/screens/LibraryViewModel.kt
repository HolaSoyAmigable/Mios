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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SongRepository(application)
    private val playerManager = PlayerManager(application)

    val songs: StateFlow<List<Song>> = repository.getCatalogedSongs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying
    val currentPosition: StateFlow<Long> = playerManager.currentPosition
    val duration: StateFlow<Long> = playerManager.duration
    val isRepeatEnabled: StateFlow<Boolean> = playerManager.isRepeatEnabled

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
        playerManager.playSong(song.mediaStoreId)
        _currentSong.value = song
    }

    fun togglePlayPause() {
        playerManager.togglePlayPause()
    }

    fun toggleRepeat() {
        playerManager.toggleRepeat()
    }

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            repository.deleteSong(song)
            if (_currentSong.value?.id == song.id) {
                _currentSong.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
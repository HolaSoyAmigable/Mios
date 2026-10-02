package com.example.mios.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mios.data.database.Song
import com.example.mios.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PlaylistRepository(application)

    fun getSongs(playlistId: Long): StateFlow<List<Song>> =
        repository.getSongsInPlaylist(playlistId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun moveSong(playlistId: Long, songId: Long, direction: Int) {
        viewModelScope.launch { repository.moveSong(playlistId, songId, direction) }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch { repository.removeSongFromPlaylist(playlistId, songId) }
    }
}
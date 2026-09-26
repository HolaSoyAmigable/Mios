package com.example.mios.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mios.data.database.AppDatabase
import com.example.mios.data.database.Genre
import com.example.mios.data.repository.MediaStoreSong
import com.example.mios.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ImportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SongRepository(application)
    private val genreDao = AppDatabase.getDatabase(application).genreDao()

    private val _unimportedSongs = MutableStateFlow<List<MediaStoreSong>>(emptyList())
    val unimportedSongs: StateFlow<List<MediaStoreSong>> = _unimportedSongs

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds

    val genres: StateFlow<List<Genre>> = genreDao.getAllGenres()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadUnimportedSongs() {
        viewModelScope.launch {
            _unimportedSongs.value = repository.getUnimportedSongs()
        }
    }

    fun toggleSelection(mediaStoreId: Long) {
        _selectedIds.value = if (mediaStoreId in _selectedIds.value) {
            _selectedIds.value - mediaStoreId
        } else {
            _selectedIds.value + mediaStoreId
        }
    }

    fun addNewGenre(name: String) {
        viewModelScope.launch {
            genreDao.insertGenre(Genre(name = name))
        }
    }

    fun confirmGenreForSelection(genreName: String) {
        viewModelScope.launch {
            val songsToImport = _unimportedSongs.value.filter { it.mediaStoreId in _selectedIds.value }
            songsToImport.forEach { song ->
                repository.catalogSong(song, genreName)
            }
            _selectedIds.value = emptySet()
            loadUnimportedSongs()
        }
    }
}
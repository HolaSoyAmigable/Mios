package com.example.mios.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mios.data.database.AppDatabase
import com.example.mios.data.database.Genre
import com.example.mios.data.database.Song
import com.example.mios.data.repository.SongRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GenresViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SongRepository(application)
    private val genreDao = AppDatabase.getDatabase(application).genreDao()

    val genres: StateFlow<List<Genre>> = genreDao.getAllGenres()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getSongsForGenre(genreName: String): StateFlow<List<Song>> =
        repository.getSongsByGenre(genreName)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
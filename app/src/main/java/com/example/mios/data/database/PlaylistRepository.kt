package com.example.mios.data.repository

import android.content.Context
import com.example.mios.data.database.AppDatabase
import com.example.mios.data.database.Playlist
import com.example.mios.data.database.PlaylistSongCrossRef
import com.example.mios.data.database.Song
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(context: Context) {
    private val playlistDao = AppDatabase.getDatabase(context).playlistDao()

    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()

    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> = playlistDao.getSongsInPlaylist(playlistId)

    suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(Playlist(name = name, createdAt = System.currentTimeMillis()))
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val position = playlistDao.countSongsInPlaylist(playlistId)
        playlistDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, songId, position))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
        reindexPositions(playlistId)
    }

    suspend fun moveSong(playlistId: Long, songId: Long, direction: Int) {
        val orderedIds = playlistDao.getSongIdsInOrder(playlistId).toMutableList()
        val index = orderedIds.indexOf(songId)
        val targetIndex = index + direction
        if (index == -1 || targetIndex < 0 || targetIndex >= orderedIds.size) return
        val temp = orderedIds[index]
        orderedIds[index] = orderedIds[targetIndex]
        orderedIds[targetIndex] = temp
        orderedIds.forEachIndexed { i, id -> playlistDao.updatePosition(playlistId, id, i) }
    }

    private suspend fun reindexPositions(playlistId: Long) {
        val orderedIds = playlistDao.getSongIdsInOrder(playlistId)
        orderedIds.forEachIndexed { i, id -> playlistDao.updatePosition(playlistId, id, i) }
    }
}
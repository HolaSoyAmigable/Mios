package com.example.mios.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_song_crossref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    @Query("SELECT COUNT(*) FROM playlist_song_crossref WHERE playlistId = :playlistId")
    suspend fun countSongsInPlaylist(playlistId: Long): Int

    @Query("""
        SELECT songs.* FROM songs
        INNER JOIN playlist_song_crossref 
        ON songs.id = playlist_song_crossref.songId
        WHERE playlist_song_crossref.playlistId = :playlistId
        ORDER BY playlist_song_crossref.position ASC
    """)
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>

    @Query("SELECT songId FROM playlist_song_crossref WHERE playlistId = :playlistId ORDER BY position ASC")
    suspend fun getSongIdsInOrder(playlistId: Long): List<Long>

    @Query("UPDATE playlist_song_crossref SET position = :position WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun updatePosition(playlistId: Long, songId: Long, position: Int)
}
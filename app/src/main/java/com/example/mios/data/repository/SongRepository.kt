package com.example.mios.data.repository

import android.content.Context
import android.provider.MediaStore
import com.example.mios.data.database.AppDatabase
import com.example.mios.data.database.Song as SongEntity
import kotlinx.coroutines.flow.Flow

data class MediaStoreSong(
    val mediaStoreId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long
)

class SongRepository(private val context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val songDao = database.songDao()
    private val playlistDao = database.playlistDao()

    fun getCatalogedSongs(): Flow<List<SongEntity>> = songDao.getAllSongs()

    fun getSongsByGenre(genre: String): Flow<List<SongEntity>> = songDao.getSongsByGenre(genre)

    suspend fun getUnimportedSongs(): List<MediaStoreSong> {
        val allDeviceSongs = readFromMediaStore()
        val importedIds = allDeviceSongs.mapNotNull { deviceSong ->
            val existing = songDao.getSongByMediaStoreId(deviceSong.mediaStoreId)
            if (existing != null) deviceSong.mediaStoreId else null
        }
        return allDeviceSongs.filter { it.mediaStoreId !in importedIds }
    }

    suspend fun catalogSong(song: MediaStoreSong, genre: String) {
        songDao.insertSong(
            SongEntity(
                mediaStoreId = song.mediaStoreId,
                title = song.title,
                artist = song.artist,
                album = song.album,
                genre = genre,
                duration = song.duration,
                dateAdded = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteSong(song: SongEntity) {
        playlistDao.removeSongFromAllPlaylists(song.id)
        songDao.deleteSong(song)
    }

    private fun readFromMediaStore(): List<MediaStoreSong> {
        val songs = mutableListOf<MediaStoreSong>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            collection, projection, selection, null, sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                songs.add(
                    MediaStoreSong(
                        mediaStoreId = cursor.getLong(idCol),
                        title = cursor.getString(titleCol) ?: "Desconocido",
                        artist = cursor.getString(artistCol) ?: "Desconocido",
                        album = cursor.getString(albumCol) ?: "Desconocido",
                        duration = cursor.getLong(durationCol)
                    )
                )
            }
        }
        return songs
    }
}
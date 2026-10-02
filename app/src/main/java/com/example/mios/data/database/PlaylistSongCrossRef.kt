package com.example.mios.data.database

import androidx.room.Entity

@Entity(
    tableName = "playlist_song_crossref",
    primaryKeys = ["playlistId", "songId"]
)
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long,
    val position: Int
)
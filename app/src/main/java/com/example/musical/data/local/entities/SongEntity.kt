package com.example.musical.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val artworkUrl: String,
    val durationMs: Int,
    val streamUrl: String? = null,
    val isLiked: Boolean = false,
    val lastPlayedAt: Long = 0L
)

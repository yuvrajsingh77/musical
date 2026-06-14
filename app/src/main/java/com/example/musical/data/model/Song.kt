package com.example.musical.data.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val artworkUrl: String,
    val durationMs: Int,
    val streamUrl: String? = null,
    val songDetailUrl: String? = null  // ADD THIS
)

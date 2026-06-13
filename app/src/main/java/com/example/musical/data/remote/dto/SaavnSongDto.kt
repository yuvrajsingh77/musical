package com.example.musical.data.remote.dto

import com.example.musical.data.model.Song

data class ItunesSearchResponse(
    val resultCount: Int,
    val results: List<ItunesSongDto>
)

data class ItunesSongDto(
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val artworkUrl100: String?,
    val previewUrl: String?,
    val trackTimeMillis: Int?
)

fun ItunesSongDto.toSong(): Song = Song(
    id = trackId?.toString() ?: "",
    title = trackName ?: "Unknown",
    artist = artistName ?: "Unknown Artist",
    album = collectionName ?: "Unknown Album",
    artworkUrl = artworkUrl100
        ?.replace("100x100", "600x600") ?: "",
    durationMs = trackTimeMillis ?: 0,
    streamUrl = previewUrl
)

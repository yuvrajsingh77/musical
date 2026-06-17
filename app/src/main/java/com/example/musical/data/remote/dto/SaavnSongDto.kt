package com.example.musical.data.remote.dto

import com.example.musical.data.model.Song

data class RailwaySearchResponse(
    val success: Boolean?,
    val results: List<RailwaySearchResult>?
)

data class RailwaySearchResult(
    val id: String?,
    val title: String?,
    val artist: String?,
    val artwork: String?,
    val duration: Int?
)

data class RailwayStreamResponse(
    val success: Boolean?,
    val data: RailwaySongData?
)

data class RailwaySongData(
    val id: String?,
    val title: String?,
    val artist: String?,
    val album: String?,
    val artwork: String?,
    val duration: Int?,
    val url: String?,
    val ext: String?
)

fun RailwaySearchResult.toSong(): Song = Song(
    id = id ?: "",
    title = title ?: "Unknown",
    artist = artist ?: "Unknown Artist",
    album = "",
    artworkUrl = artwork ?: "",
    durationMs = (duration ?: 0) * 1000,
    streamUrl = null,
    songDetailUrl = null
)

fun RailwaySongData.toSong(): Song = Song(
    id = id ?: "",
    title = title ?: "Unknown",
    artist = artist ?: "Unknown Artist",
    album = album ?: "",
    artworkUrl = artwork ?: "",
    durationMs = (duration ?: 0) * 1000,
    streamUrl = url,
    songDetailUrl = null
)

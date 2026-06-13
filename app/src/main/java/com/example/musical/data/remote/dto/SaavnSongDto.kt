package com.example.musical.data.remote.dto

import com.example.musical.data.model.Song
import com.google.gson.annotations.SerializedName

data class SaavnSearchResponse(
    val status: Boolean?,
    val results: List<SaavnSearchResult>?
)

data class SaavnSearchResult(
    val id: String?,
    val title: String?,
    val image: String?,
    val images: SaavnImages?,
    val album: String?,
    val description: String?,
    @SerializedName("more_info") val moreInfo: SaavnMoreInfo?,
    @SerializedName("perma_url") val permaUrl: String?
)

data class SaavnImages(
    @SerializedName("50x50") val small: String?,
    @SerializedName("150x150") val medium: String?,
    @SerializedName("500x500") val large: String?
)

data class SaavnMoreInfo(
    val vlink: String?,
    val singers: String?,
    val language: String?,
    @SerializedName("album_id") val albumId: String?
)

// Full song detail from /song?id=
data class SaavnSongDetailResponse(
    val status: Boolean?,
    val id: String?,
    val song: String?,
    val album: String?,
    val year: Int?,
    @SerializedName("primary_artists") val primaryArtists: String?,
    val singers: String?,
    val image: String?,
    val images: SaavnImages?,
    val duration: String?,
    @SerializedName("media_url") val mediaUrl: String?,
    @SerializedName("media_urls") val mediaUrls: SaavnMediaUrls?,
    @SerializedName("has_lyrics") val hasLyrics: Boolean?
)

data class SaavnMediaUrls(
    @SerializedName("96_KBPS") val kbps96: String?,
    @SerializedName("160_KBPS") val kbps160: String?,
    @SerializedName("320_KBPS") val kbps320: String?
)

// Parse duration "4:22" → milliseconds
fun parseDuration(duration: String?): Int {
    if (duration.isNullOrEmpty()) return 0
    return try {
        val parts = duration.split(":")
        val minutes = parts[0].toInt()
        val seconds = parts[1].toInt()
        (minutes * 60 + seconds) * 1000
    } catch (e: Exception) { 0 }
}

fun SaavnSearchResult.toSong(): Song = Song(
    id = id ?: "",
    title = title ?: "Unknown",
    artist = moreInfo?.singers
        ?: description?.split("·")?.lastOrNull()?.trim()
        ?: "Unknown Artist",
    album = album ?: "Unknown Album",
    artworkUrl = images?.large ?: images?.medium ?: image ?: "",
    durationMs = 0,
    streamUrl = moreInfo?.vlink  // preview for now, replaced with full below
)

fun SaavnSongDetailResponse.toSong(): Song = Song(
    id = id ?: "",
    title = song ?: "Unknown",
    artist = singers ?: primaryArtists ?: "Unknown Artist",
    album = album ?: "Unknown Album",
    artworkUrl = images?.large ?: images?.medium ?: image ?: "",
    durationMs = parseDuration(duration),
    streamUrl = mediaUrls?.kbps320
        ?: mediaUrls?.kbps160
        ?: mediaUrls?.kbps96
        ?: mediaUrl
)

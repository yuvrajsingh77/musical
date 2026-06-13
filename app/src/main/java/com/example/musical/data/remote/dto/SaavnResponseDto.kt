package com.example.musical.data.remote.dto

import com.example.musical.data.model.Song

data class SaavnSearchResponse(
    val success: Boolean,
    val data: SaavnSearchDataDto
)

data class SaavnSearchDataDto(
    val results: List<SaavnSongDto>
)

data class SaavnSongResponse(
    val success: Boolean,
    val data: List<SaavnSongDto>
)

data class SaavnAlbumResponse(
    val success: Boolean,
    val data: SaavnAlbumDataDto
)

data class SaavnAlbumDataDto(
    val results: List<SaavnAlbumDto>
)

data class SaavnAlbumDto(
    val id: String,
    val name: String,
    val image: List<SaavnImageDto>?,
    val artists: SaavnArtistsDto?
)

data class SaavnSongDto(
    val id: String,
    val name: String,
    val album: SaavnSongAlbumDto?,
    val artists: SaavnArtistsDto?,
    val image: List<SaavnImageDto>?,
    val downloadUrl: List<SaavnDownloadUrlDto>?,
    val duration: Int?
)

data class SaavnSongAlbumDto(
    val id: String?,
    val name: String?
)

data class SaavnArtistsDto(
    val primary: List<SaavnArtistDto>?
)

data class SaavnArtistDto(
    val id: String?,
    val name: String?
)

data class SaavnImageDto(
    val quality: String?,
    val url: String?
)

data class SaavnDownloadUrlDto(
    val quality: String?,
    val url: String?
)

fun SaavnSongDto.toSong(): Song {
    val artistName = artists?.primary?.filter { !it.name.isNullOrBlank() }?.joinToString(", ") { it.name ?: "" }
        ?: "Unknown Artist"
    val albumName = album?.name ?: "Unknown Album"
    
    // Pick highest quality image url
    val imageUrl = image?.maxByOrNull {
        it.quality?.replace("x", "")?.toIntOrNull() ?: 0
    }?.url ?: image?.lastOrNull()?.url ?: ""

    // Pick highest quality streaming url
    val stream = downloadUrl?.maxByOrNull {
        it.quality?.replace("kbps", "")?.toIntOrNull() ?: 0
    }?.url ?: downloadUrl?.lastOrNull()?.url

    return Song(
        id = id,
        title = name,
        artist = if (artistName.isBlank()) "Unknown Artist" else artistName,
        album = albumName,
        artworkUrl = imageUrl,
        durationMs = (duration ?: 0) * 1000,
        streamUrl = stream
    )
}

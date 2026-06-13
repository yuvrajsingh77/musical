package com.example.musical.data.remote

import com.example.musical.data.remote.dto.SaavnSearchResponse
import com.example.musical.data.remote.dto.SaavnSongDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    @GET("search")
    suspend fun searchSongs(
        @Query("query") query: String
    ): SaavnSearchResponse

    @GET("song")
    suspend fun getSongById(
        @Query("id") id: String
    ): SaavnSongDetailResponse

    @GET("lyrics")
    suspend fun getLyrics(
        @Query("id") id: String
    ): SaavnLyricsResponse
}

data class SaavnLyricsResponse(
    val status: Boolean?,
    val lyrics: String?
)

package com.example.musical.data.remote

import com.example.musical.data.remote.dto.SaavnAlbumResponse
import com.example.musical.data.remote.dto.SaavnSearchResponse
import com.example.musical.data.remote.dto.SaavnSongResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SaavnApiService {

    @GET("search/songs")
    suspend fun searchSongs(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): SaavnSearchResponse

    @GET("songs/{id}")
    suspend fun getSongById(
        @Path("id") id: String
    ): SaavnSongResponse

    @GET("search/albums")
    suspend fun searchAlbums(
        @Query("query") query: String
    ): SaavnAlbumResponse
}

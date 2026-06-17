package com.example.musical.data.remote

import com.example.musical.data.remote.dto.RailwaySearchResponse
import com.example.musical.data.remote.dto.RailwayStreamResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    @GET("search")
    suspend fun searchSongs(
        @Query("q") query: String
    ): RailwaySearchResponse

    @GET("stream")
    suspend fun getStream(
        @Query("q") query: String
    ): RailwayStreamResponse
}

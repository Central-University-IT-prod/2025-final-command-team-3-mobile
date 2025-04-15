package com.example.cinema.data.api

import retrofit2.http.GET
import retrofit2.http.Header

interface ProfileApiService {
    @GET("user/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileResponse
}

data class ProfileResponse(
    val email: String,
    val username: String,
    val profilePicture: String,
    val createdAt: String
)
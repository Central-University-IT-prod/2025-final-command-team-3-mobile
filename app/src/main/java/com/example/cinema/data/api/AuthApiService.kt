package com.example.cinema.data.api

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/register") // Без /api в начале!
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/yandex")
    suspend fun yandex_login(@Body request: YandexLoginRequest): AuthResponse
}

data class RegisterRequest(
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String    
)

data class AuthResponse(
    val access_token: String,
    val token_type: String
)

data class YandexLoginRequest(
    val token: String
)
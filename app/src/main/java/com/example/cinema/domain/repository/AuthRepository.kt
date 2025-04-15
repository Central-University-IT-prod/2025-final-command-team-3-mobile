package com.example.cinema.domain.repository

interface AuthRepository {
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun yandex_login(token: String): Result<Unit>
}
package com.example.cinema.data.repository

import com.example.cinema.data.api.AuthApiService
import com.example.cinema.data.api.ErrorResponse
import com.example.cinema.data.api.LoginRequest
import com.example.cinema.data.api.RegisterRequest
import com.example.cinema.data.api.YandexLoginRequest
import com.example.cinema.data.local.AuthStorage
import com.example.cinema.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val authStorage: AuthStorage
) : AuthRepository {
    private val json = Json { ignoreUnknownKeys = true }
    override suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = api.register(RegisterRequest(email, password))
            response.access_token.let { token ->
                authStorage.saveToken(token)
            }
            Result.success(Unit)
        } catch (e: HttpException) {
            // Обработка HTTP ошибок (например, 400)
            if (e.code() == 400) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = errorBody?.let {
                    try {
                        json.decodeFromString<ErrorResponse>(it)
                    } catch (ex: Exception) {
                        null
                    }
                }
                Result.failure(Exception(errorResponse?.detail ?: "Неверные данные"))
            } else {
                Result.failure(Exception("Ошибка сервера: ${e.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Неизвестная ошибка: ${e.message}"))
        }
    }

    override suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = api.login(LoginRequest(email, password))
            response.access_token.let { token ->
                authStorage.saveToken(token)
            }
            Result.success(Unit)
        } catch (e: HttpException) {
            if (e.code() == 400) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = errorBody?.let {
                    try {
                        json.decodeFromString<ErrorResponse>(it)
                    } catch (ex: Exception) {
                        null
                    }
                }
                Result.failure(Exception(errorResponse?.detail ?: "Неверные данные"))
            } else {
                Result.failure(Exception("Ошибка сервера: ${e.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Неизвестная ошибка: ${e.message}"))
        }
    }

    override suspend fun yandex_login(token: String) = withContext(Dispatchers.IO) {
        try {
            val response = api.yandex_login(YandexLoginRequest(token))
            response.access_token.let { token ->
                authStorage.saveToken(token)
            }
            Result.success(Unit)
        } catch (e: HttpException) {
            if (e.code() == 400) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = errorBody?.let {
                    try {
                        json.decodeFromString<ErrorResponse>(it)
                    } catch (ex: Exception) {
                        null
                    }
                }
                Result.failure(Exception(errorResponse?.detail ?: "Неверные данные"))
            } else {
                Result.failure(Exception("Ошибка сервера: ${e.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Неизвестная ошибка: ${e.message}"))
        }
    }
}
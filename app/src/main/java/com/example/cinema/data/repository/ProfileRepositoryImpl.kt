package com.example.cinema.data.repository

import com.example.cinema.data.api.ProfileApiService
import com.example.cinema.data.api.ProfileResponse
import com.example.cinema.data.local.AuthStorage
import com.example.cinema.domain.repository.AuthRepository
import com.example.cinema.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val profileApiService: ProfileApiService,
    private val authStorage: AuthStorage
) : ProfileRepository {
    override suspend fun getProfile(): ProfileResponse {
        val token = authStorage.getToken()
        return if (token != null) {
            profileApiService.getProfile("Bearer $token")
        } else {
            throw Exception("Unauthorized")
        }
    }
}
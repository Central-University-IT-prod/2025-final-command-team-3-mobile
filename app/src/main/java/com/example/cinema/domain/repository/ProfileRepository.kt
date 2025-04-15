package com.example.cinema.domain.repository

import com.example.cinema.data.api.ProfileResponse

interface ProfileRepository {
    suspend fun getProfile(): ProfileResponse
}
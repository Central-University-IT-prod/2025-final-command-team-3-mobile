package com.example.cinema.data.api

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val detail: String
)
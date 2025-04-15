package com.example.cinema.domain.model

import java.util.UUID

data class Film(
    val id: UUID,
    val title: String,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val adult: Boolean,
    val backdropPath: String,
    val genres: List<String>,
    val originalLanguage: String,
    val originalTitle: String,
    val popularity: Double,
    val video: Boolean,
    val status: String
)
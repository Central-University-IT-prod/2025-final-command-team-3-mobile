package com.example.cinema.domain.repository

import com.example.cinema.data.api.MetadataResponse
import com.example.cinema.domain.model.Film
import okhttp3.MultipartBody
import java.util.UUID

interface FilmRepository {
    suspend fun getTopFilms(): List<Film>
    suspend fun searchMovies(query: String): List<Film>
    suspend fun getFilmsInCollections(status: String): List<Film>
    suspend fun addToCollection(filmId: UUID, status: String)
    suspend fun updateFilmCollection(filmId: UUID, status: String)
    suspend fun deleteFilmCollection(filmId: UUID)
    suspend fun filterMovies(
        yearFrom: Int,
        yearTo: Int,
        ratingFrom: Double,
        ratingTo: Double
    ): List<Film>
    suspend fun extractMetadata(url: String): MetadataResponse
    suspend fun addFilm(title: String, description: String, posterPath: String, url: String): Film
    suspend fun uploadImage(imagePart: MultipartBody.Part): String
}

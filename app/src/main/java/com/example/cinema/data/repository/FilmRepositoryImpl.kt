package com.example.cinema.data.repository

import com.example.cinema.data.api.AddFilmRequest
import com.example.cinema.data.api.AddToCollectionRequest
import com.example.cinema.data.api.FilmApiService
import com.example.cinema.data.api.MetadataResponse
import com.example.cinema.data.local.AuthStorage
import com.example.cinema.domain.model.Film
import com.example.cinema.domain.repository.FilmRepository
import okhttp3.MultipartBody
import java.util.UUID

class FilmRepositoryImpl(
    private val filmApiService: FilmApiService,
    private val authStorage: AuthStorage
) : FilmRepository {
    override suspend fun getTopFilms(): List<Film> {
        val token = authStorage.getToken()
        return if (token != null) {
            filmApiService.getTopFilms()
        } else {
            throw Exception("Unauthorized")
        }
    }

    override suspend fun searchMovies(query: String): List<Film> {
        val token = authStorage.getToken()
        return if (token != null) {
            filmApiService.searchMovies(query)
        } else {
            throw Exception("Unauthorized")
        }
    }

    override suspend fun getFilmsInCollections(status: String): List<Film> {
        val token = authStorage.getToken()
        return if (token != null) {
            filmApiService.getCollections(status)
        } else {
            throw Exception("Unauthorized")
        }
    }

    override suspend fun addToCollection(filmId: UUID, status: String) {
        val token = authStorage.getToken()
        if (token != null) {
            filmApiService.addToCollection(AddToCollectionRequest(filmId, status))
        } else {
            throw Exception("Unauthorized")
        }
    }

    override suspend fun updateFilmCollection(filmId: UUID, status: String) {
        val token = authStorage.getToken()
        if (token != null) {
            filmApiService.updateFilmCollection(filmId.toString(), status)
        } else {
            throw Exception("Unauthorized")
        }
    }

    override suspend fun deleteFilmCollection(filmId: UUID) {
        val token = authStorage.getToken()
        if (token != null) {
            filmApiService.deleteFilmCollection(filmId.toString())
        } else {
            throw Exception("Unauthorized")
        }
    }

    override suspend fun filterMovies(
        yearFrom: Int,
        yearTo: Int,
        ratingFrom: Double,
        ratingTo: Double
    ): List<Film> {
        val token = authStorage.getToken()
        return if (token != null) {
            filmApiService.filterMovies(
                yearFrom = yearFrom,
                yearTo = yearTo,
                ratingFrom = ratingFrom,
                ratingTo = ratingTo
            )
        } else {
            throw Exception("Unauthorized")
        }
    }

    override suspend fun extractMetadata(url: String): MetadataResponse {
        val token = authStorage.getToken()
        return if (token != null) {
            filmApiService.extractMetadata(url)
        } else {
            throw Exception("Unauthorized")
        }
    }

    override suspend fun uploadImage(imagePart: MultipartBody.Part): String {
        authStorage.getToken()
            ?: throw Exception("Пользователь не авторизован")

        return filmApiService.uploadImage(imagePart)
            .body()
            ?.filename
            ?: throw Exception("Ошибка загрузки изображения")
    }

    override suspend fun addFilm(title: String, description: String, posterPath: String, url: String): Film {
        val token = authStorage.getToken()

        if (token != null) {
            return filmApiService.addFilm(
                AddFilmRequest(title, description, posterPath, url)
            ).body() ?: throw Exception("Ошибка добавления фильма")
        } else {
            throw Exception("Unauthorized")
        }
    }
}
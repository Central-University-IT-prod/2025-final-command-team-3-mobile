package com.example.cinema.data.api

import com.example.cinema.domain.model.Film
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface FilmApiService {
    @GET("movies/recommended")
    suspend fun getTopFilms(): List<Film>

    @GET("movies/search")
    suspend fun searchMovies(
        @Query("title") title: String
    ): List<Film>

    @GET("collection/")
    suspend fun getCollections(
        @Query("status") status: String
    ): List<Film>

    @POST("collection/add")
    suspend fun addToCollection(
        @Body request: AddToCollectionRequest
    )

    @POST("collection/{movie_id}/{status}")
    suspend fun updateFilmCollection(
        @Path("movie_id") movieId: String,
        @Path("status") status: String
    ): Response<Unit>

    @GET("movies/filter")
    suspend fun filterMovies(
        @Query("year_from") yearFrom: Int,
        @Query("year_to") yearTo: Int,
        @Query("rating_from") ratingFrom: Double,
        @Query("rating_to") ratingTo: Double
    ): List<Film>

    @GET("movies/extract-metadata")
    suspend fun extractMetadata(
        @Query("url") url: String
    ): MetadataResponse

    @DELETE("collection/{movie_id}")
    suspend fun deleteFilmCollection(
        @Path("movie_id") movieId: String
    ): Response<Unit>

    @POST("collection/add")
    suspend fun addFilm(
        @Body request: AddFilmRequest
    ): Response<Film>

    @Multipart
    @POST("images/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>
}

data class UploadResponse(
    val filename: String
)

data class AddFilmRequest(
    val title: String,
    val description: String,
    val posterPath: String,
    val url: String,
    val status: String = "will_watch"
)

data class AddToCollectionRequest(
    val movieId: UUID,
    val status: String
)

data class MetadataResponse(
    val title: String,
    val overview: String,
    val posterUrl: String
)
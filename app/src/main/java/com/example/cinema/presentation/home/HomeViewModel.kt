package com.example.cinema.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinema.data.api.MetadataResponse
import com.example.cinema.data.repository.FilmRepositoryImpl
import com.example.cinema.domain.model.Film
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class HomeViewModel(private val filmRepository: FilmRepositoryImpl) : ViewModel() {
    private val _movies = MutableStateFlow<List<Film>>(emptyList())
    val movies: StateFlow<List<Film>> get() = _movies

    private val _metadata = MutableStateFlow<MetadataResponse?>(null)
    val metadata: StateFlow<MetadataResponse?> get() = _metadata

    private val _filename = MutableStateFlow<String?>(null)
    val filename: StateFlow<String?> get() = _filename

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetFilmAddedState() {
        _state.update { it.copy(isFilmAdded = false) }
    }

    fun setFilename(fname: String) {
        val filename = fname
        _filename.value = filename
    }

    fun searchFilms(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = filmRepository.searchMovies(query)
                _movies.update { result }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadTopFilms() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val films = filmRepository.getTopFilms()
                _movies.value = films
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun extractMetadata(url: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = filmRepository.extractMetadata(url)
                _metadata.value = result
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun uploadImage(imagePart: MultipartBody.Part?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                if (imagePart != null) {
                    val result = filmRepository.uploadImage(imagePart)
                    _filename.value = result
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addFilm(title: String, description: String, serverFilename: String, url: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                filmRepository.addFilm(title, description ?: "", serverFilename ?: "", url)
                _state.update { it.copy(isFilmAdded = true) } // Устанавливаем флаг успешного добавления
                _filename.value = null
                _metadata.value = null
                loadTopFilms()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class HomeState(
    val isLoading: Boolean = false,
    val isFilmAdded: Boolean = false,
    val error: String? = null
)
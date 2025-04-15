package com.example.cinema.presentation.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinema.data.repository.FilmRepositoryImpl
import com.example.cinema.domain.model.Film
import com.example.cinema.domain.repository.FilmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CollectionsViewModel(
    private val filmRepository: FilmRepositoryImpl
) : ViewModel() {
    private val _films = MutableStateFlow<List<Film>>(emptyList())
    val films: StateFlow<List<Film>> = _films

    private val _state = MutableStateFlow(CollectionsState())
    val state: StateFlow<CollectionsState> = _state

    fun getFilmsInCollections(status: String) {
        viewModelScope.launch {
            _films.value = filmRepository.getFilmsInCollections(status)
        }
    }

    fun removeFilmFromList(filmId: UUID) {
        _films.value = _films.value.filter { it.id != filmId }
    }
}

data class CollectionsState(
    val isLoading: Boolean = false,
    val error: String? = null
)
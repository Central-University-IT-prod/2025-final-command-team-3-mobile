package com.example.cinema.presentation.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cinema.data.repository.FilmRepositoryImpl
import com.example.cinema.domain.repository.FilmRepository

class CollectionsViewModelFactory(
    private val filmRepository: FilmRepositoryImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionsViewModel::class.java)) {
            return CollectionsViewModel(filmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.cinema.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cinema.data.repository.FilmRepositoryImpl

class HomeViewModelFactory(
    private val filmRepository: FilmRepositoryImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(filmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
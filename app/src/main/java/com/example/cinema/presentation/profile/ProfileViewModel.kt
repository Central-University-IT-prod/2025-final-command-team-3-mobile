package com.example.cinema.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinema.data.api.ProfileResponse
import com.example.cinema.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    // ProfileResponse
    private val _profile = MutableStateFlow<ProfileResponse?>(null)
    val profile: StateFlow<ProfileResponse?> get() = _profile

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    fun loadProfile() {
        _state.value = ProfileState(isLoading = true)
        viewModelScope.launch {
            try {
                val profile = profileRepository.getProfile()

                _profile.value = profile
                _state.value = ProfileState(isLoading = false)
            } catch (e: Exception) {
                _state.value = ProfileState(error = e.message)
            }
        }
    }
}

data class ProfileState(
    val isLoading: Boolean = false,
    val error: String? = null
)
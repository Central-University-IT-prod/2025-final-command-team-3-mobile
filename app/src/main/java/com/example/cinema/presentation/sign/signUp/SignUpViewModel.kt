package com.example.cinema.presentation.sign.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinema.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state

    fun setError(errorMessage: String) {
        _state.update { it.copy(error = errorMessage) }
    }

    fun register(email: String, password: String) {
        _state.value = SignUpState(isLoading = true)
        viewModelScope.launch {
            val result = authRepository.register(email, password)
            _state.value = result.fold(
                onSuccess = { SignUpState(isSuccess = true) },
                onFailure = { SignUpState(error = it.message) }
            )
        }
    }

    fun registerWithYandex(token: String) {
        _state.value = SignUpState(isLoading = true)
        viewModelScope.launch {
            val result = authRepository.yandex_login(token)
            _state.value = result.fold(
                onSuccess = { SignUpState(isSuccess = true) },
                onFailure = { SignUpState(error = it.message) }
            )
        }
    }
}

data class SignUpState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
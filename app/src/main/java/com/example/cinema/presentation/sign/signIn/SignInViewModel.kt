package com.example.cinema.presentation.sign.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinema.domain.repository.AuthRepository
import com.example.cinema.presentation.sign.signUp.SignUpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state

    fun setError(errorMessage: String) {
        _state.update { it.copy(error = errorMessage) }
    }

    fun login(email: String, password: String) {
        _state.value = SignInState(isLoading = true)
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _state.value = result.fold(
                onSuccess = { SignInState(isSuccess = true) },
                onFailure = { SignInState(error = it.message) }
            )
        }
    }

    fun registerWithYandex(token: String, onComplete: (Result<Unit>) -> Unit) {
        _state.value = SignInState(isLoading = true)
        viewModelScope.launch {
            val result = authRepository.yandex_login(token)
            _state.value = result.fold(
                onSuccess = { SignInState(isSuccess = true) },
                onFailure = { SignInState(error = it.message) }
            )
            onComplete(result)
        }
    }
}

data class SignInState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
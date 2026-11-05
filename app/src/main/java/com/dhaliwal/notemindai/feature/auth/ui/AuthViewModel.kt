package com.dhaliwal.notemindai.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dhaliwal.notemindai.data.model.AuthRequest
import com.dhaliwal.notemindai.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI State representation for the Authentication screens (Login/Signup).
 */
sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val message: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

/**
 * ViewModel to manage UI state and handle authentication requests across screen rotations.
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(AuthRequest(username.trim(), password))
            result.fold(
                onSuccess = { response ->
                    val user = response.username ?: username.trim()
                    _uiState.value = AuthUiState.Success("Welcome back, $user!")
                },
                onFailure = { error ->
//                    val message = parseErrorMessage(error, "Invalid Credentials")
                    val message = "Invalid Credentials"
                    _uiState.value = AuthUiState.Error(message)
                }
            )
        }
    }

    fun signup(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signup(AuthRequest(username.trim(), password))
            result.fold(
                onSuccess = { response ->
                    val user = response.username ?: username.trim()
                    _uiState.value = AuthUiState.Success("Account created successfully for $user!")
                },
                onFailure = { error ->
//                    val message = parseErrorMessage(error, "Invalid Credentials")
                    val message = "Invalid Credentials"
                    _uiState.value = AuthUiState.Error(message)
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun parseErrorMessage(error: Throwable, defaultMsg: String): String {
        return when (error) {
            is HttpException -> {
                val code = error.code()
                val body = error.response()?.errorBody()?.string()
                if (!body.isNullOrBlank()) {
                    "Error $code: $body"
                } else {
                    "Error $code: ${error.message()}"
                }
            }
            is IOException -> "Network error. Please check your internet connection."
            else -> error.localizedMessage ?: defaultMsg
        }
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(authRepository) as T
        }
    }
}

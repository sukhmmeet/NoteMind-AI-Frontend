package com.dhaliwal.notemindai.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhaliwal.notemindai.feature.auth.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// These events are actions for the screen, not long-lived screen state.
sealed interface AuthEvent {
    data object Success : AuthEvent
    data class Error(val message: String) : AuthEvent
}

/**
 * Runs authentication requests and exposes only UI-friendly state.
 *
 * The ViewModel keeps requests alive across recomposition and prevents a
 * second request while the first one is still running.
 */
class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    private val _loading = kotlinx.coroutines.flow.MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun login(username: String, password: String) {
        authenticate {
            repository.login(username, password)
        }
    }

    fun signup(username: String, password: String) {
        authenticate {
            repository.signup(username, password)
        }
    }

    private fun authenticate(
        operation: suspend () -> Result<Unit>
    ) {
        // Ignore fast repeated taps while the current request is running.
        if (_loading.value) return

        viewModelScope.launch {
            _loading.value = true

            try {
                // The repository converts API failures into a Result for the UI.
                operation().fold(
                    onSuccess = {
                        _events.emit(AuthEvent.Success)
                    },
                    onFailure = {
                        _events.emit(
                            AuthEvent.Error(
                                it.message ?: "Something went wrong"
                            )
                        )
                    }
                )
            } catch (e: CancellationException) {
                throw e
            } finally {
                // Always let the user interact with the form again.
                _loading.value = false
            }
        }
    }
}

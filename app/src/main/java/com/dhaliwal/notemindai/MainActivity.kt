package com.dhaliwal.notemindai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dhaliwal.notemindai.data.model.AuthRequest
import com.dhaliwal.notemindai.data.repository.AuthRepository
import com.dhaliwal.notemindai.network.NetworkModule
import com.dhaliwal.notemindai.ui.theme.NoteMindAITheme
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * Main entrance activity for the application.
 * Sets up the Jetpack Compose user interface and initializes the AuthRepository.
 */
class MainActivity : ComponentActivity() {

    // Lazy initialization of AuthRepository via NetworkModule
    private val authRepository by lazy { NetworkModule.provideAuthRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteMindAITheme {
                Screen(authRepository)
            }
        }
    }
}

/**
 * Main UI screen for testing login/signup API calls.
 * Displays the current JWT token, Refresh token, and status messages.
 */
@Composable
fun Screen(authRepository: AuthRepository) {
    // State variables to store tokens and messages for display
    var jwt by remember { mutableStateOf("") }
    var refresh by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    val composableScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display JWT Token
        Text(text = "JWT: $jwt")
        Spacer(modifier = Modifier.height(8.dp))

        // Display Refresh Token
        Text(text = "Refresh Token: $refresh")
        Spacer(modifier = Modifier.height(8.dp))

        // Display status or error messages if present
        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Button to trigger authentication test
        Button(onClick = {
            composableScope.launch {
                statusMessage = "Logging in..."
                val result = authRepository.login(
                    AuthRequest(username = "testing", password = "password")
                )
                result.fold(
                    onSuccess = { response ->
                        jwt = response.jwtToken ?: ""
                        refresh = response.refreshToken ?: ""
                        statusMessage = "Success for user: ${response.username ?: ""}"
                    },
                    onFailure = { error ->
                        statusMessage = when (error) {
                            is HttpException -> {
                                val code = error.code()
                                val rawUrl = error.response()?.raw()?.request?.url
                                val errorBody = try {
                                    error.response()?.errorBody()?.string()
                                } catch (_: Exception) {
                                    null
                                }
                                if (!errorBody.isNullOrBlank()) {
                                    "HTTP $code: $errorBody\nURL: $rawUrl"
                                } else {
                                    "HTTP $code: ${error.message()}\nURL: $rawUrl"
                                }
                            }
                            is com.google.gson.JsonSyntaxException,
                            is com.google.gson.stream.MalformedJsonException -> "Server returned non-JSON response (check backend URL or endpoint)"
                            is IOException -> "Network error: ${error.localizedMessage}"
                            else -> error.localizedMessage ?: "An unexpected error occurred"
                        }
                    }
                )
            }
        }) {
            Text(text = "Login Test")
        }
    }
}

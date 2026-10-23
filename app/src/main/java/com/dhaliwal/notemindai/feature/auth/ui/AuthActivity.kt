
package com.dhaliwal.notemindai.feature.auth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.dhaliwal.notemindai.DashboardActivity
import com.dhaliwal.notemindai.core.network.NetworkModule
import com.dhaliwal.notemindai.core.security.AuthPreferences
import com.dhaliwal.notemindai.feature.auth.data.repository.AuthRepository
import com.dhaliwal.notemindai.ui.theme.NoteMindAITheme

class AuthActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            AuthRepository(
                NetworkModule(applicationContext),
                AuthPreferences(applicationContext)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialIsLogin = intent.getBooleanExtra(EXTRA_IS_LOGIN, true)

        setContent {
            NoteMindAITheme {
                val isLoading by authViewModel.loading.collectAsState()
                var serverError by remember { mutableStateOf("") }

                LaunchedEffect(authViewModel) {
                    authViewModel.events.collect { event ->
                        when (event) {
                            AuthEvent.Success -> {
                                startActivity(
                                    Intent(
                                        this@AuthActivity,
                                        DashboardActivity::class.java
                                    )
                                )
                                finish()
                            }

                            is AuthEvent.Error -> {
                                serverError = event.message
                            }
                        }

                    }
                }

                AuthScreen(
                    initialIsLogin = initialIsLogin,
                    isLoading = isLoading,
                    serverError = serverError,
                    onLogin = { username, password ->
                        serverError = ""
                        authViewModel.login(username, password)
                    },
                    onSignUp = { username, password ->
                        serverError = ""
                        authViewModel.signup(username, password)
                    },
                    onForgotPassword = {
                        Toast.makeText(
                            this@AuthActivity,
                            "Forgot password will be available soon.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_IS_LOGIN = "extra_is_login"
    }
}

@Composable
fun AuthScreen(
    initialIsLogin: Boolean = true,
    isLoading: Boolean = false,
    serverError: String = "",
    onLogin: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit,
    onForgotPassword: () -> Unit
) {
    var isLogin by rememberSaveable {
        mutableStateOf(initialIsLogin)
    }

    var username by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    var validationError by rememberSaveable {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLogin) "Welcome Back" else "Create Account",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isLogin) {
                "Log in to continue to NoteMind"
            } else {
                "Create your NoteMind account"
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthTextField(
            value = username,
            onValueChange = {
                username = it
                validationError = ""
            },
            label = "Username",
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = password,
            onValueChange = {
                password = it
                validationError = ""
            },
            label = "Password",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        if (isLogin) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onForgotPassword,
                    enabled = !isLoading
                ) {
                    Text("Forgot password?")
                }
            }

        } else {
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    validationError = ""
                },
                label = "Confirm password",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (validationError.isNotBlank()) {
            Text(
                text = validationError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (serverError.isNotBlank()) {
            Text(
                text = serverError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                validationError = when {
                    username.isBlank() ->
                        "Please enter your username."

                    password.isBlank() ->
                        "Please enter your password."

                    password.length < 8 ->
                        "Password must be at least 8 characters."

                    !isLogin && confirmPassword.isBlank() ->
                        "Please confirm your password."

                    !isLogin && password != confirmPassword ->
                        "Passwords do not match."

                    else -> ""
                }

                if (validationError.isEmpty()) {
                    if (isLogin) {
                        onLogin(username.trim(), password)
                    } else {
                        onSignUp(username.trim(), password)
                    }
                }

            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = if (isLogin) "Login" else "Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isLogin) {
                    "Don't have an account? "
                } else {
                    "Already have an account? "
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = {
                    isLogin = !isLogin
                    validationError = ""
                    confirmPassword = ""
                },
                enabled = !isLoading
            ) {
                Text(if (isLogin) "Sign up" else "Login")
            }
        }
    }
}

package com.dhaliwal.notemindai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dhaliwal.notemindai.feature.auth.ui.AuthActivity
import com.dhaliwal.notemindai.feature.dashboard.ui.DashboardActivity
import com.dhaliwal.notemindai.network.NetworkModule
import com.dhaliwal.notemindai.ui.theme.NoteMindAITheme

/**
 * Splash & initial authentication check screen.
 * Checks for a valid saved session/token; navigates to Dashboard if valid, otherwise to AuthActivity.
 */
class MainActivity : ComponentActivity() {

    private val authRepository by lazy { NetworkModule.provideAuthRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteMindAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                LaunchedEffect(Unit) {
                    val result = authRepository.refreshToken()
                    result.fold(
                        onSuccess = {
                            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        onFailure = {
                            val intent = Intent(this@MainActivity, AuthActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

package com.dhaliwal.notemindai.core.network

import com.dhaliwal.notemindai.feature.auth.data.remote.AuthApiService
import com.dhaliwal.notemindai.feature.auth.data.remote.ProtectedApiService
import com.dhaliwal.notemindai.feature.auth.data.remote.dto.RefreshRequest
import android.content.Context
import com.dhaliwal.notemindai.core.security.AuthPreferences
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class NetworkModule(context: Context) {

    private val appContext = context.applicationContext
    private val authPrefs = AuthPreferences(appContext)
    private val baseUrl = "https://notemind-ai-backend-6g8l.onrender.com/api/v1/"

    private val refreshLock = Any()

    private val gsonConverter = GsonConverterFactory.create()

    // Dedicated client for login, signup, logout and refresh.
    // It does not use the token authenticator.
    private val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverter)
            .build()
    }

    private val authApi: AuthApiService by lazy {
        authRetrofit.create(AuthApiService::class.java)
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val token = authPrefs.jwtToken

        val builder = request.newBuilder()

        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }

        chain.proceed(builder.build())
    }

    private val tokenAuthenticator = object : Authenticator {

        override fun authenticate(
            route: Route?,
            response: Response
        ): Request? {

            // Only allow one retry after the original request.
            if (response.responseCount() >= 2) {
                return null
            }

            synchronized(refreshLock) {

                val previousToken = response.request
                    .header("Authorization")
                    ?.removePrefix("Bearer ")

                val currentAccessToken = authPrefs.jwtToken
                    ?: return null

                // Another request may already have refreshed the token.
                // Reuse it instead of rotating the refresh token again.
                if (
                    !previousToken.isNullOrBlank() &&
                    previousToken != currentAccessToken
                ) {
                    return response.request.newBuilder()
                        .header(
                            "Authorization",
                            "Bearer $currentAccessToken"
                        )
                        .build()
                }

                val refreshToken = authPrefs.refreshToken
                    ?: return null

                val refreshResponse = try {
                    authApi.refreshToken(
                        RefreshRequest(refreshToken)
                    ).execute()
                } catch (_: IOException) {
                    // Network failure is not proof that the session
                    // is invalid. Keep tokens for a later retry.
                    return null
                }

                if (!refreshResponse.isSuccessful) {
                    if (
                        refreshResponse.code() == 401 ||
                        refreshResponse.code() == 403
                    ) {
                        authPrefs.clearSession()
                    }

                    return null
                }

                val envelope = refreshResponse.body()

                if (envelope?.success != true) {
                    return null
                }

                val tokens = envelope.data ?: return null

                if (
                    tokens.jwtToken.isBlank() ||
                    tokens.refreshToken.isBlank()
                ) {
                    return null
                }

                // Save the rotated pair together under the same lock.
                authPrefs.jwtToken = tokens.jwtToken
                authPrefs.refreshToken = tokens.refreshToken

                return response.request.newBuilder()
                    .header(
                        "Authorization",
                        "Bearer ${tokens.jwtToken}"
                    )
                    .build()
            }
        }
    }

    private val authenticatedClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    private val protectedRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(authenticatedClient)
            .addConverterFactory(gsonConverter)
            .build()
    }

    val protectedApiService: ProtectedApiService by lazy {
        protectedRetrofit.create(ProtectedApiService::class.java)
    }

    val authApiService: AuthApiService
        get() = authApi


    private fun Response.responseCount(): Int {
        var count = 1
        var priorResponse = priorResponse

        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }

        return count
    }

}

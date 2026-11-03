package com.dhaliwal.notemindai.network

import android.content.Context
import com.dhaliwal.notemindai.data.local.AuthPreferences
import com.dhaliwal.notemindai.data.repository.AuthRepository
import com.dhaliwal.notemindai.network.api.AuthApiService
import com.dhaliwal.notemindai.network.interceptor.AuthInterceptor
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton dependency provider for network components.
 * Configures Retrofit, OkHttpClient, AuthInterceptor, and AuthApiService.
 */
object NetworkModule {

    // Base URL for the production Render backend API
    private const val BASE_URL = "https://notemind-ai-backend-6g8l.onrender.com/api/v1/"

    /**
     * Creates an instance of AuthPreferences for local token storage.
     */
    fun provideAuthPreferences(context: Context): AuthPreferences {
        return AuthPreferences(context)
    }

    /**
     * Configures and creates the Retrofit AuthApiService interface.
     */
    fun provideAuthApi(context: Context): AuthApiService {
        val authPreferences = provideAuthPreferences(context)
        val authInterceptor = AuthInterceptor(authPreferences)

        // OkHttpClient with AuthInterceptor attached
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        // Gson instance with lenient parsing mode enabled
        val gson = GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .create()

        // Retrofit builder with Base URL and Gson converter
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(AuthApiService::class.java)
    }

    /**
     * Provides an AuthRepository instance configured with AuthApiService and AuthPreferences.
     */
    fun provideAuthRepository(context: Context): AuthRepository {
        val authApi = provideAuthApi(context)
        val authPreferences = provideAuthPreferences(context)
        return AuthRepository(authApi, authPreferences)
    }
}

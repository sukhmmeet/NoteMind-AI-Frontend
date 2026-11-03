package com.dhaliwal.notemindai.network.interceptor

import com.dhaliwal.notemindai.data.local.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor that runs for every HTTP request.
 * Automatically adds the User-Agent header and injects the Bearer JWT token into protected requests.
 */
class AuthInterceptor(
    private val authPreferences: AuthPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Set a standard User-Agent header to avoid 403 Forbidden responses from proxies/firewalls
        if (originalRequest.header("User-Agent") == null) {
            requestBuilder.header("User-Agent", "NoteMindAI-Android/1.0")
        }

        val path = originalRequest.url.encodedPath

        // Check if the current request is an authentication endpoint
        val isAuthEndpoint = path.contains("/auth/login") ||
                path.contains("/auth/signup") ||
                path.contains("/auth/refresh-token") ||
                path.contains("/auth/logout")

        // Attach Authorization header only if it's not an unauthenticated auth endpoint
        if (!isAuthEndpoint) {
            val token = authPreferences.getAccessToken()

            if (!token.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}

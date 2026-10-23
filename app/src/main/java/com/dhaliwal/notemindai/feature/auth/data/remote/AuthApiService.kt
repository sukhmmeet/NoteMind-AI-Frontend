package com.dhaliwal.notemindai.feature.auth.data.remote

import com.dhaliwal.notemindai.feature.auth.data.remote.dto.ApiResponse
import com.dhaliwal.notemindai.feature.auth.data.remote.dto.AuthRequest
import com.dhaliwal.notemindai.feature.auth.data.remote.dto.AuthResponseDto
import com.dhaliwal.notemindai.feature.auth.data.remote.dto.RefreshRequest
import com.dhaliwal.notemindai.feature.auth.data.remote.dto.RefreshResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/signup")
    suspend fun signup(@Body request: AuthRequest): ApiResponse<AuthResponseDto>

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): ApiResponse<AuthResponseDto>

    @POST("auth/logout")
    suspend fun logout(@Body request: RefreshRequest): ApiResponse<Nothing?>

    // Must return a Synchronous Call object for the Interceptor thread
    @POST("auth/refresh-token")
    fun refreshToken(@Body request: RefreshRequest): Call<ApiResponse<RefreshResponseDto>>
}

interface ProtectedApiService {
    @GET("users/profile")
    suspend fun getProfile(): ApiResponse<Any>
}

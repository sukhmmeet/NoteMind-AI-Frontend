package com.dhaliwal.notemindai.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val timestamp: String
)

@Serializable
data class AuthRequest(
    val username: String,
    val password: String
)

@Serializable
data class RefreshRequest(
    val refreshToken: String
)

@Serializable
data class AuthResponseDto(
    val username: String,
    val userId: Long,
    val jwtToken: String,
    val refreshToken: String
)

@Serializable
data class RefreshResponseDto(
    val jwtToken: String,
    val refreshToken: String
)

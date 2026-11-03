package com.dhaliwal.notemindai.data.model

import com.google.gson.annotations.SerializedName

/**
 * Standard backend response envelope.
 * Wraps API response metadata (success status, message, timestamp) and generic payload data.
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("timestamp")
    val timestamp: String? = null,
    @SerializedName("data")
    val data: T? = null
)

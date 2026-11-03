package com.dhaliwal.notemindai.data.model

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("timestamp")
    val timestamp: String? = null
)

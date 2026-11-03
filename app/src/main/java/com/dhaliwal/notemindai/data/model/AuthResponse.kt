package com.dhaliwal.notemindai.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("userId")
    val userId: Long? = null,
    @SerializedName("jwtToken")
    val jwtToken: String? = null,
    @SerializedName("refreshToken")
    val refreshToken: String? = null
)

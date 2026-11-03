package com.dhaliwal.notemindai.data.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    @SerializedName("jwtToken")
    val jwtToken: String? = null
)

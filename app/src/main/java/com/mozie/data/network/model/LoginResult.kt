package com.mozie.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResult {
    @SerializedName("token")
    @Expose
    val token: String? = null

    @SerializedName("expiration")
    @Expose
    val expiration: String? = null
}

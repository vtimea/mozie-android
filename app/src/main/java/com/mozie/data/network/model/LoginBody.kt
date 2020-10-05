package com.mozie.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginBody(
    @SerializedName("userId")
    @Expose
    val userId: String? = null,

    @SerializedName("accessToken")
    @Expose
    val accessToken: String? = null
)
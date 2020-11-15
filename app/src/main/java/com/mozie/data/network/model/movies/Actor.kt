package com.mozie.data.network.model.movies

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Actor {
    @SerializedName("name")
    @Expose
    val name: String? = null

    @SerializedName("pictureUrl")
    @Expose
    val pictueUrl: String? = null
}
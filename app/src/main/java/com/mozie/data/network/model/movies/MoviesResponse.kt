package com.mozie.data.network.model.movies

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MoviesResponse {
    @SerializedName("released")
    @Expose
    val released: MutableList<FeaturedMovie>? = null

    @SerializedName("unreleased")
    @Expose
    val unreleased: MutableList<FeaturedMovie>? = null
}
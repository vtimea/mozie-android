package com.mozie.data.network.model.movies

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MoviesResponse {
    @SerializedName("recommended")
    @Expose
    val recommended: MutableList<FeaturedMovie>? = null

    @SerializedName("now")
    @Expose
    val now: MutableList<FeaturedMovie>? = null

    @SerializedName("soon")
    @Expose
    val soon: MutableList<FeaturedMovie>? = null
}
package com.mozie.data.network.model.movies

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MoviesResponse {
    @SerializedName("recommended")
    @Expose
    val recommended: MutableList<Movie>? = null

    @SerializedName("now")
    @Expose
    val now: MutableList<Movie>? = null

    @SerializedName("soon")
    @Expose
    val soon: MutableList<Movie>? = null
}
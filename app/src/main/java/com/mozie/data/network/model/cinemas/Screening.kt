package com.mozie.data.network.model.cinemas

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Screening {
    @SerializedName("id")
    @Expose
    val id: String? = null

    @SerializedName("startTime")
    @Expose
    val startTime: String? = null

    @SerializedName("type")
    @Expose
    val type: String? = null

    @SerializedName("voice")
    @Expose
    val voice: String? = null

    @SerializedName("cinema_id")
    @Expose
    val cinemaId: String? = null

    @SerializedName("cinema_name")
    @Expose
    val cinemaName: String? = null

    @SerializedName("movie_id")
    @Expose
    val movieId: String? = null

    @SerializedName("movie_title")
    @Expose
    val movieTitle: String? = null
}
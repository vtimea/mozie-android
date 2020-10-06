package com.mozie.data.network.model.movies

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MovieDetail {
    @SerializedName("id")
    @Expose
    val id: String? = null

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("genre")
    @Expose
    val genre: String? = null

    @SerializedName("length")
    @Expose
    val length: Int? = null

    @SerializedName("description")
    @Expose
    val description: String? = null

    @SerializedName("posterUrl")
    @Expose
    val posterUrl: String? = null

    @SerializedName("actors")
    @Expose
    val actors: List<Actor>? = null
}
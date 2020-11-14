package com.mozie.data.network.model.cinemas

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Seat {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("row")
    @Expose
    val row: Int? = null

    @SerializedName("col")
    @Expose
    val col: Int? = null

    @SerializedName("available")
    @Expose
    val available: Boolean? = null
}
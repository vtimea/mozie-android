package com.mozie.data.network.model.cinemas

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ScreeningRoom {
    @SerializedName("numRows")
    @Expose
    val numRows: Int? = null

    @SerializedName("numCols")
    @Expose
    val numCols: Int? = null

    @SerializedName("seats")
    @Expose
    val seats: List<Seat>? = null
}
package com.mozie.data.network.model.tickets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TicketType {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("name")
    @Expose
    val name: String? = null

    @SerializedName("price")
    @Expose
    val price: Int? = null
}
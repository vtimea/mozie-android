package com.mozie.data.network.model.tickets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TicketOrder {
    @SerializedName("userId")
    @Expose
    var userId: String? = null

    @SerializedName("ticketTypes")
    @Expose
    var ticketTypes: List<Int>? = null

    @SerializedName("seats")
    @Expose
    var seats: List<Int>? = null

    @SerializedName("sumAmount")
    @Expose
    var sumAmount = 0
}
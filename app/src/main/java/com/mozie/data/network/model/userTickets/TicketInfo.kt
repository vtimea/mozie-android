package com.mozie.data.network.model.userTickets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TicketInfo {
    @SerializedName("ticketId")
    @Expose
    var ticketId = 0

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("price")
    @Expose
    var price = 0

    @SerializedName("col")
    @Expose
    var col = 0

    @SerializedName("row")
    @Expose
    var row = 0
}
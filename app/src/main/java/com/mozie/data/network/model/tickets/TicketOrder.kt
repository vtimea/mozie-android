package com.mozie.data.network.model.tickets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TicketOrder {
    @SerializedName("ticketTypes")
    @Expose
    var ticketTypes: List<Int>? = null

    @SerializedName("seats")
    @Expose
    var seats: List<Int>? = null

    @SerializedName("sumAmount")
    @Expose
    var sumAmount = 0


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TicketOrder
        if (ticketTypes != other.ticketTypes) return false
        if (seats != other.seats) return false
        if (sumAmount != other.sumAmount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ticketTypes?.hashCode() ?: 0
        result = 31 * result + (seats?.hashCode() ?: 0)
        result = 31 * result + sumAmount
        return result
    }


}
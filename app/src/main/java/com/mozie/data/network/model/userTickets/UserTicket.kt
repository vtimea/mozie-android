package com.mozie.data.network.model.userTickets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserTicket {
    @SerializedName("movieTitle")
    @Expose
    var movieTitle: String? = null

    @SerializedName("movieStartTime")
    @Expose
    var movieStartTime: String? = null

    @SerializedName("moviePosterUrl")
    @Expose
    var moviePosterUrl: String? = null

    @SerializedName("screeningType")
    @Expose
    var screeningType: String? = null

    @SerializedName("cinemaName")
    @Expose
    var cinemaName: String? = null

    @SerializedName("tickets")
    @Expose
    var tickets: List<TicketInfo>? = null
}
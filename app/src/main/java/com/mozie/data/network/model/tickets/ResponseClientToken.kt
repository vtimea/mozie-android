package com.mozie.data.network.model.tickets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseClientToken {
    @SerializedName("clientToken")
    @Expose
    val clientToken: String? = null

    @SerializedName("transactionId")
    @Expose
    val transactionId: Int? = null
}
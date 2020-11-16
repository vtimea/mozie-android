package com.mozie.data.network.model.tickets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaymentResult {
    @SerializedName("nonce")
    @Expose
    var nonce: String? = null

    @SerializedName("transactionId")
    @Expose
    var transactionId: Int? = null
}
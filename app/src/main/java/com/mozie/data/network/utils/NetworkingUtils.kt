package com.mozie.data.network.utils

import com.mozie.data.RetroClient
import com.mozie.data.network.ApiService
import retrofit2.Retrofit

object NetworkingUtils {
    private var apiService: ApiService? = null
    private var client: Retrofit? = null

    fun getInstance(): ApiService {
        if (apiService == null) {
            client = RetroClient.getRetrofitInstance()
            apiService = client!!.create(ApiService::class.java)
        }
        return apiService!!
    }

}
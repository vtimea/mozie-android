package com.mozie.data

import android.content.Context
import com.mozie.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetroClient {
    private var ROOT_URL = BuildConfig.SERVER_URL
    private var retrofitInstance: Retrofit? = null

    fun getRetrofitInstance(context: Context): Retrofit {
        if (retrofitInstance == null) {
            retrofitInstance = Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .client(getClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        return retrofitInstance!!
    }

    private fun getClient(context: Context): OkHttpClient {
        // logging
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(UnauthorizedInterceptor(context))
            .build()
    }
}
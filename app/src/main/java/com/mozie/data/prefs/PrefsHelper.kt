package com.mozie.data.prefs

interface PrefsHelper {
    fun getAccessToken(): String?

    fun getUserId(): String?

    fun getAccessTokenExpiration(): String?

    fun saveAccessToken(token: String, expiration: String, userId: String)

    fun clearAccessToken()
}
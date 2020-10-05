package com.mozie.data.prefs

interface PrefsHelper {
    fun getAccessToken(): String?

    fun getAccessTokenExpiration(): String?

    fun saveAccessToken(token: String, expiration: String)

    fun clearAccessToken()
}
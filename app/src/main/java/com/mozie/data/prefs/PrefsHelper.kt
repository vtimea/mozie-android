package com.mozie.data.prefs

interface PrefsHelper {
    fun getAccessToken(): String?

    fun saveAccessToken(token: String)
}
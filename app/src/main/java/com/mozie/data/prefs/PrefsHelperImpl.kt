package com.mozie.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.mozie.data.prefs.PrefsHelperImpl.Keys.KEY_TOKEN
import com.mozie.data.prefs.PrefsHelperImpl.Keys.KEY_TOKEN_EXP
import com.mozie.data.prefs.PrefsHelperImpl.Keys.PREFS_ACCESS
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class PrefsHelperImpl @Inject constructor(@ApplicationContext context: Context) : PrefsHelper {
    object Keys {
        const val PREFS_ACCESS = "access_info"
        const val KEY_TOKEN = "access_token"
        const val KEY_TOKEN_EXP = "access_token_exp"
    }

    private val mPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_ACCESS, Context.MODE_PRIVATE)

    override fun getAccessToken(): String? {
        return mPreferences.getString(KEY_TOKEN, null)
    }

    override fun getAccessTokenExpiration(): String? {
        return mPreferences.getString(KEY_TOKEN_EXP, null)
    }

    override fun saveAccessToken(token: String, expiration: String) {
        mPreferences.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_TOKEN_EXP, expiration)
            .apply()
    }

    override fun clearAccessToken() {
        mPreferences.edit().clear().apply()
    }
}
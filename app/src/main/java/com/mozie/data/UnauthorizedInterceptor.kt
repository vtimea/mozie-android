package com.mozie.data

import android.content.Context
import android.content.Intent
import com.mozie.ui.login.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response

class UnauthorizedInterceptor(private val context: Context) : Interceptor {
    private val UNAUTHORIZED_CODES: List<Int> = listOf(
        401
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val code = response.code()
        if (UNAUTHORIZED_CODES.contains(code)) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra(LoginActivity.EXTRA_LOGOUT, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        return response
    }
}
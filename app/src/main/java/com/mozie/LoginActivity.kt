package com.mozie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager

class LoginActivity : AppCompatActivity() {
    val callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}
package com.mozie.ui.login

interface LoginNavigator {

    fun loginSuccess()

    fun handleError(message: String)
}
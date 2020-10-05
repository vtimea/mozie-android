package com.mozie.data.network.utils

abstract class Callback<T> {
    abstract fun returnResult(t: T)
    abstract fun returnError(t: Throwable)
}
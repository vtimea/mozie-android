package com.mozie.data.network.utils

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class DefaultObserver<T>(private val callback: Callback<T>) : Observer<T> {
    var disposable: Disposable? = null

    override fun onComplete() { /* do nothing */
    }

    override fun onSubscribe(d: Disposable) {
        disposable = d
    }

    override fun onNext(t: T) {
        callback.returnResult(t)
    }

    override fun onError(t: Throwable) {
        callback.returnError(t)
    }
}

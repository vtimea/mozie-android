package com.mozie.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {
    protected val disposables: MutableList<Disposable> = mutableListOf()

    override fun onCleared() {
        for (d in disposables) {
            if (!d.isDisposed) {
                d.dispose()
            }
        }
        super.onCleared()
    }
}
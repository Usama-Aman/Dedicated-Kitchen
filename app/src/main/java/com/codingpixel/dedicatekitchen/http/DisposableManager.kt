package com.codingpixel.dedicatekitchen.http

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class DisposableManager {


    companion object{
        private var compositeDisposable: CompositeDisposable? = null

        fun add(disposable: Disposable) {
            getCompositeDisposable()!!.add(disposable)
        }

        fun dispose() {
            getCompositeDisposable()!!.clear()
        }

        fun remove(disposable: Disposable)
        {
            getCompositeDisposable()!!.remove(disposable)

        }

        private fun getCompositeDisposable(): CompositeDisposable? {
            if (compositeDisposable == null || compositeDisposable!!.isDisposed) {
                compositeDisposable = CompositeDisposable()
            }
            return compositeDisposable
        }

    }
}
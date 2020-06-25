package com.yts.domain.usecase.base

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class UseCase<T> {

    protected var lastDisposable: Disposable? = null
    protected val compositeDisposable = CompositeDisposable()

    internal abstract fun buildUseCase(): Observable<T>

    /**
     * Unit type은 자바의 void와 같은 역할을 합니다.
     * 공통 인자
     */
    open fun getData(
        onSuccess: ((t: T) -> Unit),
        onError: ((t: Throwable) -> Unit),
        onFinished: () -> Unit = {}
    ) {
        disposeLast()
        lastDisposable = buildUseCase()
            .doAfterTerminate(onFinished)
            .subscribe(onSuccess, onError)

        lastDisposable?.let {
            compositeDisposable.add(it)
        }
    }


    fun disposeLast() {
        lastDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    fun dispose() {
        compositeDisposable.clear()
    }
}
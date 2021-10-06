package com.maxst.msf.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun <R> ViewModel.build(): Provider<ViewModel, R> {
    return Provider(this)
}

class Provider<T: ViewModel, R> constructor (
    val t: T
) {
    private var main: CoroutineDispatcher = Dispatchers.Main
    private var job: CoroutineDispatcher = Dispatchers.IO
    private var prepare: (() -> Unit)? = null
    private var error: ((e: Exception) -> Unit)? = null
    private var terminate: (() -> Unit)? = null
    private var subscribe: ((r: R) -> Unit)? = null

    fun action(p: () -> R) {
        t.viewModelScope.launch (job){
            try {
                prepare?.let{ launch(main) { it.invoke() } }
                val r = p.invoke()
                subscribe?.let{ launch(main) { it.invoke(r) } }
            } catch (e: Exception) {
                error?.let{ launch(main) { it.invoke(e) } }
            } finally {
                terminate?.let{ launch(main) { it.invoke() } }
            }
        }
    }

    fun fetch(p: suspend (() -> R)) {
        t.viewModelScope.launch (job){
            try {
                prepare?.let{ launch(main) { it.invoke() } }
                val r = p.invoke()
                subscribe?.let{ launch(main) { it.invoke(r) } }
            } catch (e: Exception) {
                error?.let{ launch(main) { it.invoke(e) } }
            } finally {
                terminate?.let{ launch(main) { it.invoke() } }
            }
        }
    }

    fun setMainCoroutine(p: CoroutineDispatcher): Provider<T, R> {
        main = p
        return this
    }

    fun setJobCoroutine(p: CoroutineDispatcher): Provider<T, R> {
        job = p
        return this
    }

    fun onPrepare(p: (() -> Unit)? = null): Provider<T, R> {
        prepare = p
        return this
    }

    fun onError(p: ((e: Exception) -> Unit)? = null): Provider<T, R> {
        error = p
        return this
    }

    fun onTerminate(p: (() -> Unit)? = null): Provider<T, R> {
        terminate = p
        return this
    }

    fun onSubscribe(p: ((r: R) -> Unit)? = null): Provider<T, R> {
        subscribe = p
        return this
    }
}
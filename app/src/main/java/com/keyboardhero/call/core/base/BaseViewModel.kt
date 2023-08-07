package com.keyboardhero.call.core.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : Any, E> : ViewModel(), StoreObservable<S> {
    private val store by lazy {
        ViewStateStore(this.initState())
    }

    val currentState: S
        get() = store.state

    private val _events = MutableSharedFlow<E>(extraBufferCapacity = 1)
    val events: SharedFlow<E>
        get() = _events

    abstract fun initState(): S

    override fun <T> observe(owner: LifecycleOwner, selector: (S) -> T, observer: Observer<T>) {
        store.observe(owner, selector, observer)
    }

    protected fun dispatchState(state: S) {
        store.dispatchState(state = state)
    }

    protected fun dispatchEvent(event: E) {
        _events.tryEmit(event)
    }

    fun observeEvent(
        lifecycleScope: CoroutineScope,
        viewLifecycleOwner: LifecycleOwner,
        listenState: Lifecycle.State = Lifecycle.State.CREATED,
        collector: FlowCollector<E>
    ) {
        lifecycleScope.launch {
            if (listenState == Lifecycle.State.INITIALIZED) {
                events.collect(collector)
            } else {
                viewLifecycleOwner.repeatOnLifecycle(listenState) {
                    events.collect(collector)
                }
            }
        }
    }
}

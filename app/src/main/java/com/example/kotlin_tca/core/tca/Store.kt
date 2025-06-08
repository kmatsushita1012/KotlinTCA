package com.example.kotlin_tca.core.tca

import android.util.Log
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn


interface StoreOf<State, Action> {
    val state: StateFlow<State>
    fun send(action: Action)
    fun <ChildState, ChildAction>scope(
        lens: Lens<State, ChildState?>,
        prism: Prism<Action, ChildAction>,
    ): ScopedStore<ChildState, ChildAction>?{

        val current = state.value
        val childState = lens.get(current) ?: return null

        val childStateFlow = state
            .mapNotNull { lens.get(it) }
            .distinctUntilChanged()
            .stateIn(
                CoroutineScope(Dispatchers.Main),
                SharingStarted.Eagerly,
                initialValue = childState
            )
        return ScopedStore<ChildState, ChildAction>(
            state = childStateFlow,
            sendAction = { childAction ->
                this.send(prism.embed(childAction))
            }
        )
    }

}

class Store<State, Action>(
    initialState: State,
    private val reducer: ReducerOf<State, Action>,
) : StoreOf<State, Action> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<State> = _state
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun send(action: Action) {
        val (newState, effect) = reducer.reduce(_state.value, action)
        _state.value = newState
        coroutineScope.launch {
            effect.collect { newAction ->
                send(newAction)
            }
        }
    }
}

class ScopedStore<ChildState, ChildAction>(
    override val state: StateFlow<ChildState>,
    private val sendAction: (ChildAction) -> Unit
) : StoreOf<ChildState, ChildAction> {

    override fun send(action: ChildAction) {
        sendAction(action)
    }
}



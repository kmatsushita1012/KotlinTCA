package com.example.kotlin_tca.counter

import com.example.kotlin_tca.core.tca.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.material3.*

object CounterFeature : ReducerOf<CounterFeature.State, CounterFeature.Action> {

    data class State(val count: Int = 0)

    sealed class Action {
        object Increment : Action()
        object Decrement : Action()
        object DelayedIncrement : Action()
        object OnIncrementFinished : Action()
    }

    override fun body(): ReducerOf<State, Action> =
    Reduce { state, action ->
        when (action) {
            is Action.Increment -> state.copy(count = state.count + 1) to Effect.none()
            is Action.Decrement -> state.copy(count = state.count - 1) to Effect.none()
            is Action.DelayedIncrement -> state to Effect.run { send ->
                delay(2000)
                send(Action.OnIncrementFinished)
            }
            is Action.OnIncrementFinished -> state.copy(count = state.count + 1) to Effect.none()
        }
    }
}

@Composable
fun CounterScreen(store: StoreOf<CounterFeature.State, CounterFeature.Action>) {
    val state by store.state.collectAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Count: ${state.count}")
        Row {
            Button(onClick = { store.send(CounterFeature.Action.Decrement) }) {
                Text("-")
            }
            Button(onClick = { store.send(CounterFeature.Action.Increment) }) {
                Text("+")
            }
        }
        Button(onClick = { store.send(CounterFeature.Action.DelayedIncrement) }) {
            Text("Delayed +1")
        }
    }
}

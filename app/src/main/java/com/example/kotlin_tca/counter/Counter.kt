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
        object IncrementTapped : Action()
        object DecrementTapped : Action()
        object DismissTapped : Action()
    }

    override fun body(): ReducerOf<State, Action> =
    Reduce { state, action ->
        when (action) {
            is Action.IncrementTapped -> state.copy(count = state.count + 1) to Effect.none()
            is Action.DecrementTapped -> state.copy(count = state.count - 1) to Effect.none()
            is Action.DismissTapped -> state to Effect.none()
        }
    }
}

@Composable
fun CounterScreen(store: StoreOf<CounterFeature.State, CounterFeature.Action>) {
    val state by store.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Count: ${state.count}")
        Row {
            Button(onClick = { store.send(CounterFeature.Action.DecrementTapped) }) {
                Text("-")
            }
            Button(onClick = { store.send(CounterFeature.Action.IncrementTapped) }) {
                Text("+")
            }
        }
        Button(onClick = { store.send(CounterFeature.Action.DismissTapped) }) {
            Text("Dismiss")
        }
    }
}

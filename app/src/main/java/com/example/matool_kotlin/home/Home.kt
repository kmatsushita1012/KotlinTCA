import android.util.Log
import com.example.kotlin_tca.core.tca.*
import com.example.kotlin_tca.counter.CounterFeature
import com.example.kotlin_tca.counter.CounterScreen
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.material3.*

object HomeFeature : ReducerOf<HomeFeature.State, HomeFeature.Action> {
    data class State(
        val title: String = "Home",
        val counter: CounterFeature.State? = null
    )

    sealed class Action {
        data class SetTitle(val title: String) : Action()
        class CounterButtonTapped() : Action()
        data class Counter(val action: CounterFeature.Action) : Action()
    }

    val counterLens = Lens<HomeFeature.State, CounterFeature.State?>(
        get = { it.counter },
        set = { parent, child -> parent.copy(counter = child) }
    )
    val counterPrism = Prism<HomeFeature.Action, CounterFeature.Action>(
        extract = { (it as? Action.Counter)?.action },
        embed = { Action.Counter(it) }
    )

    override fun body(): ReducerOf<State, Action> =
        Reduce<State, Action>{ state, action ->
            when (action) {
                is Action.SetTitle -> state.copy(title = action.title) to Effect.none()
                is Action.CounterButtonTapped->{
                    state.copy(counter = CounterFeature.State(1)) to Effect.none()
                }
                is Action.Counter -> {
                    when (val innerAction = action.action) {
                        is CounterFeature.Action.DelayedIncrement -> {
                            state.copy(counter = null) to Effect.none()
                        }
                        else -> state to Effect.none()
                    }
                }
            }
        } +
        OptionalScope(
            stateLens = counterLens,
            actionPrism = counterPrism,
            reducer = CounterFeature
        )
}
@Composable
fun HomeScreen(store: StoreOf<HomeFeature.State, HomeFeature.Action>) {
    val state by store.state.collectAsState()
    Log.d("DEBUG", "Home")
    val counterStore = remember(state.counter) {
        store.scope(
            lens = HomeFeature.counterLens,
            prism = HomeFeature.counterPrism
        )
    }

    FullScreenNavigation(
        item = counterStore,
        screenContent = { store ->
            CounterScreen(store)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(state.title)
            Button(onClick = {
                store.send(HomeFeature.Action.CounterButtonTapped())
            }) {
                Text("Go to Counter")
            }
            Button(onClick = {
                store.send(HomeFeature.Action.SetTitle(state.title+"A"))
            }) {
                Text("Title")
            }
        }
    }
}

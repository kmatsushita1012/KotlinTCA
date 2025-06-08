package com.example.kotlin_tca.core.tca

import androidx.compose.runtime.*

@Composable
fun <ParentState, ParentAction, ChildState, ChildAction> Navigation(
    store: StoreOf<ParentState, ParentAction>,
    lens: Lens<ParentState, ChildState?>,
    prism: Prism<ParentAction, ChildAction>,
    child: @Composable (StoreOf<ChildState, ChildAction>) -> Unit,
    parent: @Composable () -> Unit
) {
    val parentState by store.state.collectAsState()
    val childState = lens.get(parentState)

    val childStore = remember(childState) {
        store.scope(lens = lens, prism = prism)
    }

    if (childStore != null) {
        child(childStore)
    } else {
        parent()
    }
}

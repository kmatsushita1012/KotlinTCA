package com.example.kotlin_tca.core.tca

interface ReducerOf<State, Action> {
    fun body(): ReducerOf<State, Action>

    fun reduce(state: State, action: Action): Pair<State, Effect<Action>> {
        return body().reduce(state, action)
    }
}


fun <S, A> Reduce(block: (S, A) -> Pair<S, Effect<A>>): ReducerOf<S, A> {
    return object : ReducerOf<S, A> {
        override fun body() = this
        override fun reduce(state: S, action: A) = block(state, action)
    }
}

fun <ParentState, ParentAction, ChildState, ChildAction> Scope(
    stateLens: Lens<ParentState, ChildState>,
    actionPrism: Prism<ParentAction, ChildAction>,
    reducer: ReducerOf<ChildState, ChildAction>
): ReducerOf<ParentState, ParentAction> {
    return object : ReducerOf<ParentState, ParentAction> {
        override fun body() = this

        override fun reduce(parentState: ParentState, parentAction: ParentAction): Pair<ParentState, Effect<ParentAction>> {
            val childAction = actionPrism.extract(parentAction) ?: return parentState to Effect.none()
            val childState = stateLens.get(parentState)

            val (newChildState, childEffect) = reducer.reduce(childState, childAction)
            val newParentState = stateLens.set(parentState, newChildState)
            val newParentEffect = childEffect.map { actionPrism.embed(it) }

            return newParentState to newParentEffect
        }
    }
}

fun <ParentState, ParentAction, ChildState, ChildAction> OptionalScope(
    stateLens: Lens<ParentState, ChildState?>,
    actionPrism: Prism<ParentAction, ChildAction>,
    reducer: ReducerOf<ChildState, ChildAction>
): ReducerOf<ParentState, ParentAction> {
    return object : ReducerOf<ParentState, ParentAction> {
        override fun body() = this

        override fun reduce(parentState: ParentState, parentAction: ParentAction): Pair<ParentState, Effect<ParentAction>> {
            val childAction = actionPrism.extract(parentAction) ?: return parentState to Effect.none()
            val childState = stateLens.get(parentState) ?: return parentState to Effect.none()

            val (newChildState, childEffect) = reducer.reduce(childState, childAction)
            val newParentState = stateLens.set(parentState, newChildState)
            val newParentEffect = childEffect.map { actionPrism.embed(it) }

            return newParentState to newParentEffect
        }
    }
}



operator fun <S, A> ReducerOf<S, A>.plus(
    other: ReducerOf<S, A>
): ReducerOf<S, A> {
    return object : ReducerOf<S, A> {
        override fun body() = this
        override fun reduce(state: S, action: A): Pair<S, Effect<A>> {
            val (s1, e1) = this@plus.reduce(state, action)
            val (s2, e2) = other.reduce(s1, action)
            return s2 to Effect.merge(e1, e2)
        }
    }
}


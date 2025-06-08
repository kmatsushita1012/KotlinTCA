package com.example.kotlin_tca.core.tca

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class Effect<A> private constructor(
    private val flow: Flow<A>
) {

    // map は Swift の map と同じく Effect を変換する
    fun <B> map(transform: (A) -> B): Effect<B> =
        Effect(flow.map(transform))

    suspend fun collect(collector: suspend (A) -> Unit) = flow.collect(collector)

    companion object {
        fun <A> none(): Effect<A> = Effect(emptyFlow())
        fun <A> just(value: A): Effect<A> = Effect(flowOf(value))
        fun <A> merge(vararg effects: Effect<A>): Effect<A> =
            Effect(merge(*effects.map { it.flow }.toTypedArray()))
        fun <A> run(
            dispatcher: CoroutineDispatcher = Dispatchers.Default,
            block: suspend (suspend (A) -> Unit) -> Unit
        ): Effect<A> {
            val flow = flow {
                coroutineScope {
                    block { action -> emit(action) }
                }
            }.flowOn(dispatcher)
            return Effect(flow)
        }
    }
}

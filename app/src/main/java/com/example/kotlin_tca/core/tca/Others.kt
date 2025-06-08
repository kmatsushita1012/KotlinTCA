package com.example.kotlin_tca.core.tca
// --- Lens: イミュータブルStateのgetter/setterをまとめた型 ---
class Lens<S, A>(
    val get: (S) -> A,
    val set: (S, A) -> S
) {
    fun <B> compose(other: Lens<A, B>): Lens<S, B> = Lens(
        get = { s -> other.get(this.get(s)) },
        set = { s, b -> this.set(s, other.set(this.get(s), b)) }
    )
}

// --- Prism: 親Actionから子Actionの抽出と包み込み ---
class Prism<ParentAction, ChildAction>(
    val extract: (ParentAction) -> ChildAction?,
    val embed: (ChildAction) -> ParentAction
)




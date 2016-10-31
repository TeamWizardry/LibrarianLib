package com.teamwizardry.librarianlib.common.util

class FakeMap<K, V>(val getter: (K) -> V, val setter: (K, V) -> Unit) {

    operator fun set(key: K, value: V) {
        setter(key, value)
    }

    operator fun get(key: K): V {
        return getter(key)
    }

}

// to avoid the Integer object overhead.
class FakeList<V>(val getter: (Int) -> V, val setter: (Int, V) -> Unit) {

    operator fun set(key: Int, value: V) {
        setter(key, value)
    }

    operator fun get(key: Int): V {
        return getter(key)
    }

}

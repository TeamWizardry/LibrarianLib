package com.teamwizardry.librarianlib.util

import java.util.HashMap

class DefaultedMap<K, V>(protected var defaultValue: V) : HashMap<K, V>() {

    override operator fun get(k: Any?): V {
        return (this as java.util.Map<K, V>).getOrDefault(k, defaultValue)
    }

    companion object {
        private val serialVersionUID = 5615718886973854633L
    }
}

package com.teamwizardry.librarianlib.gui

import java.util.*

class Key private constructor(val character: Char, val keyCode: Int) {

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + keyCode
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        return keyCode == (other as Key).keyCode
    }

    companion object {

        /**
         * Maps a keycode to a key
         */
        private val pool = HashMap<Int, Key>()

        @JvmStatic
        operator fun get(character: Char, keyCode: Int): Key {
            val key = pool[keyCode] ?: Key(character, keyCode)
            if (!pool.containsKey(keyCode))
                pool.put(keyCode, key)
            return key
        }
    }

}

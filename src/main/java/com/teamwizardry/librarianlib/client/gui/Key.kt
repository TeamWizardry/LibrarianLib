package com.teamwizardry.librarianlib.client.gui

import java.util.*

class Key private constructor(val character: Char, val keyCode: Int) {

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + keyCode
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj)
            return true
        if (obj == null)
            return false
        if (javaClass != obj.javaClass)
            return false
        val other = obj as Key
        return keyCode == other.keyCode
    }

    companion object {

        /**
         * Maps a keycode to a key
         */
        private val pool = HashMap<Int, Key>()

        operator fun get(character: Char, keyCode: Int): Key {
            var key = pool[keyCode] ?: Key(character, keyCode)
            if (!pool.containsKey(keyCode))
                pool.put(keyCode, key)
            return key
        }
    }

}

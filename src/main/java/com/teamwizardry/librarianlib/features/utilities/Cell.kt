package com.teamwizardry.librarianlib.features.utilities

/**
 * A simple wrapper object.
 */
class Cell<T>(val value: T) {
    override fun equals(other: Any?): Boolean {
        if(other !is Cell<*>)
            return false
        return value ==  other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value.toString()
    }
}

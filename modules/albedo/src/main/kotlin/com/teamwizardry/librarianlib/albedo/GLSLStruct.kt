package com.teamwizardry.librarianlib.albedo

import dev.thecodewarrior.mirror.Mirror

abstract class GLSLStruct {
}

class GLSLStructArray<T: GLSLStruct>(clazz: Class<T>, val length: Int) {
    private val values: Array<Any>

    init {
        val constructor = Mirror.reflectClass(clazz).getDeclaredConstructor()
        values = Array(length) { constructor<T>() }
    }

    operator fun get(index: Int): T {
        @Suppress("UNCHECKED_CAST")
        return values[index] as T
    }
}

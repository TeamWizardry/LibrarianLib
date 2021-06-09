package com.teamwizardry.librarianlib.albedo


public abstract class GLSLStruct

public class GLSLStructArray<T: GLSLStruct>(public val length: Int, factory: () -> T) {
    private val values: Array<Any> = Array(length) { factory() }

    public operator fun get(index: Int): T {
        @Suppress("UNCHECKED_CAST")
        return values[index] as T
    }
}

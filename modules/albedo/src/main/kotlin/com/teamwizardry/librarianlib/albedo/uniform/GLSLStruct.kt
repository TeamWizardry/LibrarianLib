package com.teamwizardry.librarianlib.albedo.uniform

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView

public abstract class GLSLStruct(name: String) : AbstractUniform(name) {
    private val _fields = mutableListOf<AbstractUniform>()
    public val fields: List<AbstractUniform> = _fields.unmodifiableView()

    protected fun <T : AbstractUniform> add(field: T): T {
        _fields.add(field)
        return field
    }
}

public class GLSLStructArray<T : GLSLStruct>(name: String, public val length: Int, factory: (Int) -> T) :
    AbstractUniform(name) {
    private val values: Array<Any> = Array(length) { factory(it) }

    public operator fun get(index: Int): T {
        @Suppress("UNCHECKED_CAST")
        return values[index] as T
    }
}

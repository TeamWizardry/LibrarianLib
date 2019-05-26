@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.features.shader.uniforms

import com.teamwizardry.librarianlib.features.shader.Shader
import org.lwjgl.opengl.ARBShaderObjects

class UniformFloat(val defaultValue: Float): Uniform(UniformType.FLOAT) {

    override fun loadDefault() = ifBound { set(defaultValue) }

    fun set(value: Float) = ifBound {
        ARBShaderObjects.glUniform1fARB(location, value)
    }

    inline fun set(value: Number) {
        set(value.toFloat())
    }
}

class UniformFloatTime(): Uniform(UniformType.FLOAT) {

    override fun loadDefault() = ifBound {
        val nanos = System.nanoTime()
        var seconds = nanos.toDouble() / 1000000000.0
        seconds %= 100000.0
        set(seconds)
    }

    fun set(value: Float) = ifBound {
        ARBShaderObjects.glUniform1fARB(location, value)
    }

    inline fun set(value: Number) {
        set(value.toFloat())
    }
}

class UniformFloatVec2(val defaultX: Float, val defaultY: Float): Uniform(UniformType.FLOAT_VEC2) {

    override fun loadDefault() = ifBound { set(defaultX, defaultY) }

    fun set(x: Float, y: Float) = ifBound {
        ARBShaderObjects.glUniform2fARB(location, x, y)
    }

    inline fun set(x: Number, y: Number) {
        set(x.toFloat(), y.toFloat())
    }
}

class UniformFloatVec3(val defaultX: Float, val defaultY: Float, val defaultZ: Float): Uniform(UniformType.FLOAT_VEC3) {

    override fun loadDefault() = ifBound { set(defaultX, defaultY, defaultZ) }

    fun set(x: Float, y: Float, z: Float) = ifBound {
        ARBShaderObjects.glUniform3fARB(location, x, y, z)
    }

    inline fun set(x: Number, y: Number, z: Number) {
        set(x.toFloat(), y.toFloat(), z.toFloat())
    }
}

class UniformFloatVec4(val defaultX: Float, val defaultY: Float, val defaultZ: Float, val defaultW: Float): Uniform(UniformType.FLOAT_VEC4) {

    override fun loadDefault() = ifBound { set(defaultX, defaultY, defaultZ, defaultW) }

    fun set(x: Float, y: Float, z: Float, w: Float) = ifBound {
        ARBShaderObjects.glUniform4fARB(location, x, y, z, w)
    }

    inline fun set(x: Number, y: Number, z: Number, w: Number) {
        set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    }
}

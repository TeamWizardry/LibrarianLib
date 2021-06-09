package com.teamwizardry.librarianlib.albedo.uniform

import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack

public class SamplerUniform(glConstant: Int, public val textureTarget: Int) : Uniform(glConstant) {
    internal var textureUnit: Int = 0

    private var value: Int = 0

    public fun get(): Int = value
    public fun set(value: Int) {
        this.value = value
    }

    override fun push() {
        GL20.glUniform1i(location, textureUnit)
    }

}

public class SamplerArrayUniform(glConstant: Int, public val textureTarget: Int, length: Int) :
    ArrayUniform(glConstant, length) {
    internal var textureUnits: IntArray = IntArray(length)

    private val values: IntArray = IntArray(length)

    public operator fun get(index: Int): Int = values[index]
    public operator fun set(index: Int, value: Int) {
        values[index] = value
    }

    override fun push() {
        MemoryStack.stackPush().use { stack ->
            val units = stack.mallocInt(trueLength)
            for (i in 0 until trueLength) {
                units.put(textureUnits[i])
            }
            units.rewind()
            GL20.glUniform1iv(location, units)
        }
    }
}


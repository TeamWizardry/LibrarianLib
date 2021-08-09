package com.teamwizardry.librarianlib.albedo.shader.uniform

import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack

public class SamplerUniform(name: String, glConstant: Int, public val textureTarget: Int) : Uniform(name, glConstant) {
    internal var textureUnit: Int = 0

    private var value: Int = 0

    public fun get(): Int = value
    public fun set(value: Int) {
        this.value = value
    }
    public fun set(texture: Identifier) {
        this.value = Client.textureManager.getTexture(texture).glId
    }

    override fun push() {
        GL20.glUniform1i(location, textureUnit)
    }

}

public class SamplerArrayUniform(name: String, glConstant: Int, public val textureTarget: Int, length: Int) :
    ArrayUniform(name, glConstant, length) {
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


package com.teamwizardry.librarianlib.features.shader.uniforms

import com.teamwizardry.librarianlib.features.shader.Shader
import org.lwjgl.opengl.ARBShaderObjects

class UniformInt(val defaultValue: Int): Uniform(UniformType.INT) {

    override fun loadDefault() = ifBound { set(defaultValue) }

    fun set(value: Int) = ifBound {
        ARBShaderObjects.glUniform1iARB(location, value)
    }
}

class UniformIntVec2(val defaultX: Int, val defaultY: Int): Uniform(UniformType.INT_VEC2) {

    override fun loadDefault() = ifBound { set(defaultX, defaultY) }

    fun set(x: Int, y: Int) = ifBound {
        ARBShaderObjects.glUniform2iARB(location, x, y)
    }
}

class UniformIntVec3(val defaultX: Int, val defaultY: Int, val defaultZ: Int): Uniform(UniformType.INT_VEC3) {

    override fun loadDefault() = ifBound { set(defaultX, defaultY, defaultZ) }

    fun set(x: Int, y: Int, z: Int) = ifBound {
        ARBShaderObjects.glUniform3iARB(location, x, y, z)
    }
}

class UniformIntVec4(val defaultX: Int, val defaultY: Int, val defaultZ: Int, val defaultW: Int): Uniform(UniformType.INT_VEC4) {

    override fun loadDefault() = ifBound { set(defaultX, defaultY, defaultZ, defaultW) }

    fun set(x: Int, y: Int, z: Int, w: Int) = ifBound {
        ARBShaderObjects.glUniform4iARB(location, x, y, z, w)
    }
}


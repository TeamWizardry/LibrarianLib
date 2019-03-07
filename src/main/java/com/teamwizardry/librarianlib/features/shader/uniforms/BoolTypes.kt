package com.teamwizardry.librarianlib.features.shader.uniforms

import com.teamwizardry.librarianlib.features.shader.Shader
import org.lwjgl.opengl.ARBShaderObjects

class UniformBool(val defaultValue: Boolean): Uniform(UniformType.BOOL) {

    override fun loadDefault() = ifBound { set(defaultValue) }

    fun set(value: Boolean) = ifBound {
        ARBShaderObjects.glUniform1iARB(location, if (value) 1 else 0)
    }
}

class UniformBoolVec2(val defaultX: Boolean, val defaultY: Boolean): Uniform(UniformType.BOOL_VEC2) {

    override fun loadDefault() = ifBound { set(defaultX, defaultY) }

    fun set(x: Boolean, y: Boolean) = ifBound {
        ARBShaderObjects.glUniform2iARB(location, if (x) 1 else 0, if (y) 1 else 0)
    }
}

class UniformBoolVec3(val defaultX: Boolean, val defaultY: Boolean, val defaultZ: Boolean): Uniform(UniformType.BOOL_VEC3) {

    override fun loadDefault() = ifBound { set(defaultX, defaultY, defaultZ) }

    fun set(x: Boolean, y: Boolean, z: Boolean) = ifBound {
        ARBShaderObjects.glUniform3iARB(location, if (x) 1 else 0, if (y) 1 else 0, if (z) 1 else 0)
    }
}

class UniformBoolVec4(val defaultX: Boolean, val defaultY: Boolean, val defaultZ: Boolean, val defaultW: Boolean): Uniform(UniformType.BOOL_VEC4) {

    override fun loadDefault() = ifBound { set(defaultX, defaultY, defaultZ, defaultW) }

    fun set(x: Boolean, y: Boolean, z: Boolean, w: Boolean) = ifBound {
        ARBShaderObjects.glUniform4iARB(location, if (x) 1 else 0, if (y) 1 else 0, if (z) 1 else 0, if (w) 1 else 0)
    }
}


package com.teamwizardry.librarianlib.fx.shader.uniforms

import com.teamwizardry.librarianlib.fx.shader.Shader
import org.lwjgl.opengl.ARBShaderObjects

class IntTypes {

    class Int(owner: Shader, name: String, type: UniformType, size: kotlin.Int, location: kotlin.Int) : Uniform(owner, name, type, size, location) {

        fun set(value: kotlin.Int) {
            ARBShaderObjects.glUniform1iARB(location, value)
        }
    }

    class IntVec2(owner: Shader, name: String, type: UniformType, size: kotlin.Int, location: kotlin.Int) : Uniform(owner, name, type, size, location) {

        fun set(x: kotlin.Int, y: kotlin.Int) {
            ARBShaderObjects.glUniform2iARB(location, x, y)
        }
    }

    class IntVec3(owner: Shader, name: String, type: UniformType, size: kotlin.Int, location: kotlin.Int) : Uniform(owner, name, type, size, location) {

        fun set(x: kotlin.Int, y: kotlin.Int, z: kotlin.Int) {
            ARBShaderObjects.glUniform3iARB(location, x, y, z)
        }
    }

    class IntVec4(owner: Shader, name: String, type: UniformType, size: kotlin.Int, location: kotlin.Int) : Uniform(owner, name, type, size, location) {

        fun set(x: kotlin.Int, y: kotlin.Int, z: kotlin.Int, w: kotlin.Int) {
            ARBShaderObjects.glUniform4iARB(location, x, y, z, w)
        }
    }

}

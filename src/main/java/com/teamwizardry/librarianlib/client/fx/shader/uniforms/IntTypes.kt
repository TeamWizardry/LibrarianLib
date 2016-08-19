package com.teamwizardry.librarianlib.client.fx.shader.uniforms

import com.teamwizardry.librarianlib.client.fx.shader.Shader
import org.lwjgl.opengl.ARBShaderObjects

class IntTypes {

    class IntUniform(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(value: Int) {
            ARBShaderObjects.glUniform1iARB(location, value)
        }
    }

    class IntVec2Uniform(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(x: Int, y: Int) {
            ARBShaderObjects.glUniform2iARB(location, x, y)
        }
    }

    class IntVec3Uniform(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(x: Int, y: Int, z: Int) {
            ARBShaderObjects.glUniform3iARB(location, x, y, z)
        }
    }

    class IntVec4Uniform(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(x: Int, y: Int, z: Int, w: Int) {
            ARBShaderObjects.glUniform4iARB(location, x, y, z, w)
        }
    }

}

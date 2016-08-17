package com.teamwizardry.librarianlib.fx.shader.uniforms

import com.teamwizardry.librarianlib.fx.shader.Shader
import org.lwjgl.opengl.ARBShaderObjects

class IntTypes {

    class Int1(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(value: Int) {
            ARBShaderObjects.glUniform1iARB(location, value)
        }
    }

    class IntVec2(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(x: Int, y: Int) {
            ARBShaderObjects.glUniform2iARB(location, x, y)
        }
    }

    class IntVec3(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(x: Int, y: Int, z: Int) {
            ARBShaderObjects.glUniform3iARB(location, x, y, z)
        }
    }

    class IntVec4(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(x: Int, y: Int, z: Int, w: Int) {
            ARBShaderObjects.glUniform4iARB(location, x, y, z, w)
        }
    }

}

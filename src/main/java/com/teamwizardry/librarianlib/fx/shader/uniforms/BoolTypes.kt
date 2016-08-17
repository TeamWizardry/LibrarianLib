package com.teamwizardry.librarianlib.fx.shader.uniforms

import com.teamwizardry.librarianlib.fx.shader.Shader
import org.lwjgl.opengl.ARBShaderObjects

class BoolTypes {

    class BoolUniform(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(value: Boolean) {
            ARBShaderObjects.glUniform1iARB(location, if (value) 1 else 0)
        }
    }

    class BoolVec2Uniform(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(x: Boolean, y: Boolean) {
            ARBShaderObjects.glUniform2iARB(location, if (x) 1 else 0, if (y) 1 else 0)
        }
    }

    class BoolVec3Uniform(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(x: Boolean, y: Boolean, z: Boolean) {
            ARBShaderObjects.glUniform3iARB(location, if (x) 1 else 0, if (y) 1 else 0, if (z) 1 else 0)
        }
    }

    class BoolVec4Uniform(owner: Shader, name: String, type: UniformType, size: Int, location: Int) : Uniform(owner, name, type, size, location) {

        fun set(x: Boolean, y: Boolean, z: Boolean, w: Boolean) {
            ARBShaderObjects.glUniform4iARB(location, if (x) 1 else 0, if (y) 1 else 0, if (z) 1 else 0, if (w) 1 else 0)
        }
    }

}

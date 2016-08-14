package com.teamwizardry.librarianlib.fx.shader.uniforms

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.fx.shader.Shader

abstract class Uniform(val program: Shader, val name: String, val type: UniformType, val size: Int, val location: Int) {

    class NoUniform(owner: Shader, name: String, type: UniformType,
                    size: Int, location: Int) : Uniform(owner, name, type, size, location) {
        init {
            LibrarianLog.warn("[Shader %s] Uniform `%s` has unsupported type `%s`", if (owner == null) "!!NULL!!" else owner.glName, name, type.name)
        }
    }

    companion object {

        val NONE = NoUniform(Shader.NONE, "NONE", UniformType.NONE, 0, 0)
    }

}

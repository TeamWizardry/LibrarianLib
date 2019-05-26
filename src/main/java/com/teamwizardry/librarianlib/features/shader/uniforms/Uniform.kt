package com.teamwizardry.librarianlib.features.shader.uniforms

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.shader.Shader

abstract class Uniform(val type: UniformType) {
    var program: Shader = Shader.NONE
        internal set
    var name: String = ""
        internal set
    var size: Int = 1
        internal set
    var location: Int = -1
        internal set

    abstract fun loadDefault()

    fun ifBound(func: () -> Unit) {
        if(location >= 0) func()
    }
}

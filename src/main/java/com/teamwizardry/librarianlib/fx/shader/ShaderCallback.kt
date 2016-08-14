package com.teamwizardry.librarianlib.fx.shader

@FunctionalInterface
interface ShaderCallback<T : Shader> {

    fun call(shader: T)

}

package com.teamwizardry.librarianlib.client.fx.shader

@FunctionalInterface
interface ShaderCallback<T : Shader> {

    fun call(shader: T)

}

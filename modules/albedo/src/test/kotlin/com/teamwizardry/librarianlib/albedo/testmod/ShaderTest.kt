package com.teamwizardry.librarianlib.albedo.testmod

import com.teamwizardry.librarianlib.albedo.Shader

abstract class ShaderTest {
    protected abstract val shader: Shader
    protected abstract fun doDraw()

    private var compiled = false
    private var crashed = false

    fun draw() {
        if(!compiled) {
            compiled = true
            try {
                shader.delete()
                shader.compile()
                crashed = false
            } catch(e: Exception) {
                logger.error("", e)
                crashed = true
            }
        }
        if(crashed) return
        doDraw()
    }

    fun delete() {
        shader.delete()
        compiled = false
    }
}
package com.teamwizardry.librarianlib.fx.shader

import com.teamwizardry.librarianlib.fx.shader.uniforms.FloatTypes

object LibShaders {
    @JvmStatic
    val HUE = HueShader(null, "/assets/librarianlib/shaders/hue.frag")

    init {
        ShaderHelper.addShader(HUE)
    }

    class HueShader(vert: String?, frag: String?) : Shader(vert, frag) {

        var hue: FloatTypes.FloatUniform? = null

        override fun initUniforms() {
            hue = getUniform<FloatTypes.FloatUniform>("hue")
        }

        override fun uniformDefaults() {
            hue?.set(0f)
        }

    }
}

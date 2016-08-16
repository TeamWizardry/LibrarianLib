package com.teamwizardry.librarianlib.fx.shader

import com.teamwizardry.librarianlib.fx.shader.uniforms.FloatTypes

enum class LibShaders constructor() {
    INSTANCE;

    init {
        initShaders()
    }

    private fun initShaders() {
        HUE = HueShader(null, "/assets/librarianlib/shaders/hue.frag")
        ShaderHelper.addShader(HUE)
    }

    class HueShader(vert: String?, frag: String?) : Shader(vert, frag) {

        var hue: FloatTypes.Float? = null

        override fun initUniforms() {
            hue = getUniform<FloatTypes.Float>("hue")
        }

        override fun uniformDefaults() {
            hue?.set(0f)
        }

    }

    companion object {
        lateinit var HUE: Shader
    }
}

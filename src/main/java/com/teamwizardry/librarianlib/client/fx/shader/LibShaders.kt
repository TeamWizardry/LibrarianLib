package com.teamwizardry.librarianlib.client.fx.shader

import com.teamwizardry.librarianlib.client.fx.shader.uniforms.FloatTypes
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object LibShaders {
    @JvmField
    val HUE = HueShader(null, "shaders/hue.frag")

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

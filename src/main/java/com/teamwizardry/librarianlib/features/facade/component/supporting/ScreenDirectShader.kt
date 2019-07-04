package com.teamwizardry.librarianlib.features.facade.component.supporting

import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.shader.Shader
import com.teamwizardry.librarianlib.features.shader.ShaderHelper
import com.teamwizardry.librarianlib.features.shader.uniforms.FloatTypes
import net.minecraft.util.ResourceLocation

object ScreenDirectShader : Shader(null, ResourceLocation("librarianlib:shaders/screen_direct.frag")) {
    init {
        ShaderHelper.addShader(this)
    }

    private var displaySize: FloatTypes.FloatVec2Uniform? = null

    override fun initUniforms() {
        super.initUniforms()
        displaySize = getUniform("displaySize")
    }

    override fun uniformDefaults() {
        super.uniformDefaults()
        displaySize?.set(
            Client.minecraft.displayWidth.toFloat(),
            Client.minecraft.displayHeight.toFloat()
        )
    }
}
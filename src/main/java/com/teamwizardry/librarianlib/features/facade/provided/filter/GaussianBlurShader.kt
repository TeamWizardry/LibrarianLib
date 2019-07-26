package com.teamwizardry.librarianlib.features.facade.provided.filter

import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.shader.Shader
import com.teamwizardry.librarianlib.features.shader.ShaderHelper
import com.teamwizardry.librarianlib.features.shader.uniforms.BoolTypes
import com.teamwizardry.librarianlib.features.shader.uniforms.FloatTypes
import com.teamwizardry.librarianlib.features.shader.uniforms.IntTypes
import com.teamwizardry.librarianlib.features.shader.uniforms.Uniform
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.ARBTextureRg
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.DoubleBuffer
import kotlin.math.exp

object GaussianBlurShader : Shader(null, ResourceLocation("librarianlib:shaders/gaussian_blur.frag")) {
    private val kernelTexture = GL11.glGenTextures()
    private val kernelArray = generateGaussianKernel(64)

    init {
        GL11.glEnable(GL11.GL_TEXTURE_1D)
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, kernelTexture)
        val buffer = ByteBuffer.allocateDirect(kernelArray.size*4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        kernelArray.forEachIndexed { i, it ->
            buffer.put(i, it.toFloat())
        }
        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexImage1D(GL11.GL_TEXTURE_1D, 0, GL11.GL_RED, kernelArray.size, 0, GL11.GL_RED, GL11.GL_FLOAT, buffer)
        GL11.glDisable(GL11.GL_TEXTURE_1D)
    }

    var sigma: Int = 0
    var horizontal: Boolean = true

    private var displaySize: FloatTypes.FloatVec2Uniform? = null
    private var sigmaUniform: IntTypes.IntUniform? = null
    private var horizontalUniform: BoolTypes.BoolUniform? = null
    private var image: Uniform? = null
    private var kernel: Uniform? = null

    override fun initUniforms() {
        super.initUniforms()
        displaySize = getUniform("displaySize")
        sigmaUniform = getUniform("sigma")
        horizontalUniform = getUniform("horizontal")
        image = getUniform("image")
        kernel = getUniform("kernel")
    }

    override fun uniformDefaults() {
        super.uniformDefaults()
        displaySize?.set(
            Client.minecraft.displayWidth.toFloat(),
            Client.minecraft.displayHeight.toFloat()
        )
        sigmaUniform?.set(sigma)
        horizontalUniform?.set(horizontal)
    }

    fun bindTexture(texture: Int) {
        GL11.glEnable(GL11.GL_TEXTURE_1D)
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE2)
        GlStateManager.bindTexture(texture)

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE3)
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, kernelTexture)

        // these have to occur _after_ the textures are bound
        image?.also { image ->
            GL20.glUniform1i(image.location, 2)
        }
        kernel?.also { kernel ->
            GL20.glUniform1i(kernel.location, 3)
        }

        // return to the default texture unit
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
    }

    init {
        ShaderHelper.addShader(this)
    }

    // credit: https://community.khronos.org/t/dinamic-gaussian-kernel/53011/2
    private fun generateGaussianKernel(sigma: Int): DoubleArray {
        val kernel = DoubleArray(2 * sigma + 2)
        var sum = 0.0

        if (sigma == 0) {
            kernel[0] = 1.0
        } else {
            for (i in 0 until kernel.size) {
                //result = exp(-(i*i)/(double)(2*sigma*sigma)) / (sqrt(2*PI)*sigma);
                // NOTE: dividing (sqrt(2*PI)*sigma) is not needed because normalizing result later
                val result = exp(-(i * i) / (2 * sigma * sigma).toDouble())
                kernel[i] = result
                sum += result
                if (i != 0)
                    sum += result
            }

            // normalize kernel
            // make sum of all elements in kernel to 1
            for (i in 0 until kernel.size) {
                kernel[i] /= sum
            }
        }

        return kernel
    }
}
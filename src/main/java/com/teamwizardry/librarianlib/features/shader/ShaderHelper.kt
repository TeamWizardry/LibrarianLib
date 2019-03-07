package com.teamwizardry.librarianlib.features.shader

import com.google.common.base.Throwables
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.core.common.LibLibConfig
import com.teamwizardry.librarianlib.features.shader.uniforms.UniformFloat
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.function.Consumer

/**
 * Credit to Vazkii (https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/client/core/helper/ShaderHelper.java)
 */

@SideOnly(Side.CLIENT)
object ShaderHelper {

    fun init() {
        initShaders()
        ClientRunnable.registerReloadHandler { initShaders() }
    }

    private val shaders = ArrayList<Shader>()
    private var hasLoaded = false

    fun <T : Shader> addShader(shader: T): T {
        shaders.add(shader)
        if (hasLoaded && useShaders())
            createProgram(shader)
        return shader
    }

    fun initShaders() {
        if (!useShaders())
            return

        for (shader in shaders)
            createProgram(shader)

        hasLoaded = true
    }

    fun useShader(shader: Shader?) {
        if (!useShaders())
            return
        if (shader == null) {
            ARBShaderObjects.glUseProgramObjectARB(0)
            return
        }
        ARBShaderObjects.glUseProgramObjectARB(shader.glName)
        shader.handles.values.forEach { it.loadDefault() }
    }

    fun releaseShader() {
        useShader(null)
    }

    //http://hastebin.com/ameremuqev.avrasm
    fun useShaders(): Boolean {
        try {
            return FMLCommonHandler.instance().effectiveSide == Side.CLIENT && LibLibConfig.shaders && OpenGlHelper.shadersSupported
        } catch (ignored: NoSuchFieldError) {
            return false
        }
    }

    private fun createProgram(shader: Shader): Int {
        val vert = createShader(shader.vert, ARBVertexShader.GL_VERTEX_SHADER_ARB)
        val frag = createShader(shader.frag, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB)

        if (shader.glName != 0)
            GL20.glDeleteProgram(shader.glName) // Don't know if this works... but uploading it with the same id doesn't.
        val program: Int = ARBShaderObjects.glCreateProgramObjectARB()

        if (program == 0)
            return 0

        if (vert != null) ARBShaderObjects.glAttachObjectARB(program, vert)
        if (frag != null) ARBShaderObjects.glAttachObjectARB(program, frag)

        ARBShaderObjects.glLinkProgramARB(program)
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            LibrarianLog.error(getLogInfo(program))
            return 0
        }

        ARBShaderObjects.glValidateProgramARB(program)
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            LibrarianLog.error(getLogInfo(program))
            return 0
        }
        LibrarianLog.info("Created program $program - VERT:'${shader.vert}' FRAG:'${shader.frag}'")

        shader.loadUniforms(program)

        return program
    }

    private fun createShader(resource: ResourceLocation?, shaderType: Int): Int? {
        if(resource == null) return null
        val text = readText(resource)

        var shader = 0
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType)

            if (shader == 0)
                return 0

            ARBShaderObjects.glShaderSourceARB(shader, text)
            ARBShaderObjects.glCompileShaderARB(shader)

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
                throw RuntimeException("Error creating shader: " + getLogInfo(shader))
            }

            return shader
        } catch (e: Exception) {
            ARBShaderObjects.glDeleteObjectARB(shader)
            e.printStackTrace()
            return null
        }

    }

    // Most of the code taken from the LWJGL wiki
    // http://lwjgl.org/wiki/index.php?title=GLSL_Shaders_with_LWJGL

    private fun getLogInfo(obj: Int): String {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB))// + "\n" + fileText;
    }

    private fun readText(resource: ResourceLocation): String {
        return Minecraft.getMinecraft().resourceManager.getResource(resource).inputStream.bufferedReader().readText()
    }
}

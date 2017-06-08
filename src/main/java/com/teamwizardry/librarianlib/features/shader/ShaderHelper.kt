package com.teamwizardry.librarianlib.features.shader

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.core.common.LibLibConfig
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.renderer.OpenGlHelper
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

    private val VERT = ARBVertexShader.GL_VERTEX_SHADER_ARB
    private val FRAG = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB
    private val shaders = ArrayList<Shader>()
    private var hasLoaded = false

    fun <T : Shader> addShader(shader: T): T {
        shaders.add(shader)
        if (hasLoaded && !useShaders())
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

    fun <T : Shader> useShader(shader: T?, callback: Consumer<T>?) {
        if (shader == null) {
            ARBShaderObjects.glUseProgramObjectARB(0)
            return
        }
        if (!useShaders())
            return


        ARBShaderObjects.glUseProgramObjectARB(shader.glName)

        if (shader.time != null) {
            val nanos = System.nanoTime()
            var seconds = nanos.toDouble() / 1000000000.0
            seconds %= 100000.0
            shader.time?.set(seconds)
        }

        shader.uniformDefaults()

        callback?.accept(shader)
    }

    fun <T : Shader> useShader(shader: T?) {
        useShader(shader, null)
    }

    fun releaseShader() {
        useShader<Shader>(null)
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
        val vert = shader.vert
        val frag = shader.frag

        var vertId = 0
        var fragId = 0
        val program: Int
        var vertText: String
        var fragText: String
        if (vert != null) {
            try {
                vertText = readFileAsString(vert)
                vertId = createShader(vertText, VERT)
            } catch (e: Exception) {
                vertText = "ERROR: \n" + e.toString()
                for (elem in e.stackTrace) {
                    vertText += "\n" + elem.toString()
                }
            }

        }
        if (frag != null) {
            try {
                fragText = readFileAsString(frag)
                fragId = createShader(fragText, FRAG)
            } catch (e: Exception) {
                fragText = "ERROR: \n" + e.toString()
                for (elem in e.stackTrace) {
                    fragText += "\n" + elem.toString()
                }
            }

        }

        if (shader.glName != 0)
            GL20.glDeleteProgram(shader.glName) // Don't know if this works... but uploading it with the same id doesn't.
        program = ARBShaderObjects.glCreateProgramObjectARB()
        if (program == 0)
            return 0

        if (vert != null)
            ARBShaderObjects.glAttachObjectARB(program, vertId)
        if (frag != null)
            ARBShaderObjects.glAttachObjectARB(program, fragId)

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
        LibrarianLog.info("Created program %d - VERT:'%s' FRAG:'%s'", program, vert, frag)

        shader.init(program)

        return program
    }

    private fun createShader(fileText: String, shaderType: Int): Int {
        var shader = 0
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType)

            if (shader == 0)
                return 0

            ARBShaderObjects.glShaderSourceARB(shader, fileText)
            ARBShaderObjects.glCompileShaderARB(shader)

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
                throw RuntimeException("Error creating shader: " + getLogInfo(shader))
            }

            return shader
        } catch (e: Exception) {
            ARBShaderObjects.glDeleteObjectARB(shader)
            e.printStackTrace()
            return -1
        }

    }

    // Most of the code taken from the LWJGL wiki
    // http://lwjgl.org/wiki/index.php?title=GLSL_Shaders_with_LWJGL

    private fun getLogInfo(obj: Int): String {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB))// + "\n" + fileText;
    }

    @Throws(Exception::class)
    private fun readFileAsString(filename: String): String {
        val source = StringBuilder()
        val `in` = ShaderHelper::class.java.getResourceAsStream(filename)
        var exception: Exception? = null
        val reader: BufferedReader

        if (`in` == null)
            return ""

        try {
            reader = BufferedReader(InputStreamReader(`in`, "UTF-8"))

            var innerExc: Exception? = null
            try {
                var line: String? = reader.readLine()
                while (line != null) {
                    source.append(line).append('\n')
                    line = reader.readLine()
                }
            } catch (exc: Exception) {
                exception = exc
            }

            try {
                reader.close()
            } catch (exc: Exception) {
                if (innerExc == null)
                    innerExc = exc
                else
                    exc.printStackTrace()
            }

            if (innerExc != null)
                throw innerExc
        } catch (exc: Exception) {
            exception = exc
        } finally {
            try {
                `in`.close()
            } catch (exc: Exception) {
                if (exception == null)
                    exception = exc
                else
                    exc.printStackTrace()
            }

            if (exception != null)
                throw exception
        }

        return source.toString()
    }

}

package com.teamwizardry.librarianlib.albedo

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.bridge.IRenderTypeState
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.GlResourceGc
import com.teamwizardry.librarianlib.core.util.ISimpleReloadListener
import com.teamwizardry.librarianlib.core.util.kotlin.weakSetOf
import com.teamwizardry.librarianlib.core.util.resolveSibling
import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20.*
import java.util.LinkedList
import kotlin.math.min

public abstract class Shader(
    /**
     * An arbitrary name used for logging
     */
    public val shaderName: String,
    /**
     * The location of the vertex shader, if any
     */
    public val vertexName: ResourceLocation?,
    /**
     * The location of the fragment shader, if any
     */
    public val fragmentName: ResourceLocation?
) {
    /**
     * The OpenGL handle for the shader program
     */
    public var glProgram: Int by GlResourceGc.track(this, 0) { GlStateManager.deleteProgram(it) }
        private set

    /**
     * True if this shader is currently bound. This only tracks calls to [bind] and [unbind], so modifications of the
     * bound shader outside of that will not be reflected. Binding another LibrarianLib shader will unbind this one.
     */
    public val isBound: Boolean
        get() = currentlyBound === this

    /**
     * A [RenderState] object that can be added to a [RenderType.State] to bind, push uniforms, and unbind this shader
     * program when drawing. Doing so requires the [IRenderTypeState] mixin, so cast to that and call
     * [IRenderTypeState.addState].
     *
     * **NOTE!!** If your shader uses samplers (textures), your texture state *must not be set*, or else it may
     * inadvertently overwrite one of the textures the shader is using.
     *
     * ```java
     * RenderType.State renderState = RenderType.State.getBuilder()
     *     .alpha(...) // set up your state normally
     *     .build(...);
     *
     * ((IRenderTypeState)renderState).addState(someShader.getRenderState());
     *
     * RenderType type = RenderType.makeState(..., renderState);
     * ```
     */
    public val renderState: RenderState = object: RenderState("enable_$shaderName", {
        bind()
    }, {
        unbind()
    }) {}

    /**
     * Called after the shader is bound and before uniforms are pushed
     */
    protected open fun setupState() {}

    /**
     * Called before the shader is unbound
     */
    protected open fun teardownState() {}

    private var uniforms: List<GLSL>? = null

    private val boundTextureUnits = mutableMapOf<Pair<Int, Int>, Int>()

    /**
     * Binds this as the current program and pushes the current uniform states. If possible, [renderState] is the
     * preferred method of binding shaders.
     */
    public fun bind() {
        currentlyBound?.unbind()
        GlStateManager.useProgram(glProgram)
        currentlyBound = this
        if (uniforms == null && glProgram != 0) {
            uniforms = UniformBinder.bindAllUniforms(this, glProgram)
        }

        var currentUnit = FIRST_TEXTURE_UNIT
        boundTextureUnits.clear()
        uniforms?.forEach {
            if (it is GLSL.GLSLSampler) {
                it.textureUnit = boundTextureUnits.getOrPut(it.get() to it.textureTarget) { currentUnit++ }
            } else if (it is GLSL.GLSLSampler.GLSLSamplerArray) {
                for (i in 0 until min(it.length, it.trueLength)) {
                    it.textureUnits[i] = boundTextureUnits.getOrPut(it[i] to it.textureTarget) { currentUnit++ }
                }
            }
        }
        boundTextureUnits.forEach { (tex, unit) ->
            bindTexture(tex.first, tex.second, unit)
        }
        RenderSystem.activeTexture(GL13.GL_TEXTURE0)

        setupState()
        uniforms?.forEach {
            it.push()
        }
    }

    /**
     * Unbinds this shader program and cleans up some GL texture state.
     */
    public fun unbind() {
        teardownState()
        GlStateManager.useProgram(0)
        boundTextureUnits.forEach { (tex, unit) ->
            unbindTexture(tex.second, unit)
        }
        currentlyBound = null
    }

    private fun bindTexture(texture: Int, target: Int, unit: Int) {
        if (unit < 8 && target == GL11.GL_TEXTURE_2D) { // GlStateManager only tracks the first 8 GL_TEXTURE_2D units
            RenderSystem.activeTexture(GL13.GL_TEXTURE0 + unit)
            RenderSystem.enableTexture()
            RenderSystem.bindTexture(texture)
            RenderSystem.activeTexture(GL13.GL_TEXTURE0)
        } else {
            if (unit < 8) {
                // GlStateManager tracks the first 8 texture units, and it only changes the texture when the value
                // changes from its perspective. This becomes an issue if we change the texture without it knowing,
                // since it may think that a texture doesn't need to be re-bound. To alleviate this we set it to a
                // dummy value, making sure it will always try to re-bind next time someone binds a texture.
                RenderSystem.activeTexture(GL13.GL_TEXTURE0 + unit)
                RenderSystem.enableTexture()
                Client.textureManager.bindTexture(ResourceLocation("librarianlib:albedo/textures/dummy.png"))
            }
            RenderSystem.activeTexture(GL13.GL_TEXTURE0) // get GlStateManager into a known state
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)
            GL11.glEnable(target)
            GL11.glBindTexture(target, texture)
            GL13.glActiveTexture(GL13.GL_TEXTURE0) // make sure we return to that state
        }
    }

    private fun unbindTexture(target: Int, unit: Int) {
        if (unit < 8 && target == GL11.GL_TEXTURE_2D) { // GlStateManager only tracks the first 8 GL_TEXTURE_2D units
            RenderSystem.activeTexture(GL13.GL_TEXTURE0 + unit)
            RenderSystem.bindTexture(0)
            RenderSystem.disableTexture()
            RenderSystem.activeTexture(GL13.GL_TEXTURE0)
        } else {
            if (unit < 8) {
                // GlStateManager tracks the first 8 texture units, and it only changes the texture when the value
                // changes from its perspective. This becomes an issue if we change the texture without it knowing,
                // since it may think that a texture doesn't need to be re-bound. To alleviate this we set it to a
                // dummy value, making sure it will always try to re-bind next time someone binds a texture.
                RenderSystem.activeTexture(GL13.GL_TEXTURE0 + unit)
                Client.textureManager.bindTexture(ResourceLocation("librarianlib:albedo/textures/dummy.png"))
                RenderSystem.bindTexture(0)
                RenderSystem.disableTexture()
            }
            RenderSystem.activeTexture(GL13.GL_TEXTURE0) // get GlStateManager into a known state
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)

            GL11.glEnable(target)
            GL11.glBindTexture(target, 0)
            GL11.glBindTexture(GL13.GL_TEXTURE_2D, 0)
            GL11.glDisable(target)
            GL13.glActiveTexture(GL13.GL_TEXTURE0) // make sure we return to that state
        }
    }

    public fun delete() {
        GlStateManager.deleteProgram(glProgram)
        glProgram = 0
    }

    /**
     * The base source string number. Sufficiently high that _hopefully_ a substitution in the error log will be
     * correct, sufficiently low so even a signed short won't overflow, and sufficiently different from the max signed
     * short value that anyone using that max value in their code won't have collisions
     */
    private val sourceNumberBase = 31500

    init {
        @Suppress("LeakingThis")
        allShaders.add(this)
        compile()
    }

    @JvmSynthetic
    internal fun compile() {
        compile(Client.resourceManager)
    }

    private fun compile(resourceManager: IResourceManager) {
        logger.info("Compiling shader program $shaderName")
        if (uniforms != null) {
            UniformBinder.unbindAllUniforms(this)
            uniforms = null
        }
        var vertexHandle = 0
        var fragmentHandle = 0
        try {
            if (vertexName != null) {
                val files = mutableMapOf<ResourceLocation, Int>()
                vertexHandle = compileShader(GL_VERTEX_SHADER, "vertex",
                    readShader(resourceManager, vertexName, files), vertexName, files)
            }
            if (fragmentName != null) {
                val files = mutableMapOf<ResourceLocation, Int>()
                fragmentHandle = compileShader(GL_FRAGMENT_SHADER, "fragment",
                    readShader(resourceManager, fragmentName, files), fragmentName, files)
            }
            GlStateManager.deleteProgram(glProgram)
            glProgram = linkProgram(vertexHandle, fragmentHandle)
        } finally {
            if (glProgram != 0) {
                glDetachShader(glProgram, vertexHandle)
                glDetachShader(glProgram, fragmentHandle)
            }
            GlStateManager.deleteShader(vertexHandle)
            GlStateManager.deleteShader(fragmentHandle)
        }
        logger.debug("Finished compiling shader program $shaderName")
    }

    private fun readShader(
        resourceManager: IResourceManager, name: ResourceLocation,
        files: MutableMap<ResourceLocation, Int>,
        stack: LinkedList<ResourceLocation> = LinkedList()
    ): String {
        if (name in stack) {
            val cycleString = stack.reversed().joinToString(" -> ") { if (it == name) "[$it" else "$it" } + " -> $name]"
            throw ShaderCompilationException("#import cycle: $cycleString")
        }
        stack.push(name)
        val sourceNumber = files.getOrPut(name) { sourceNumberBase + files.size }
        val importRegex = """^\s*#pragma\s+import\s*<\s*(\S*)\s*>\s*$""".toRegex()

        val text = Client.getResourceText(resourceManager, name)
        var out = ""
        text.lineSequence().forEachIndexed { i, line ->
            val lineNumber = i + 1
            val importMatch = importRegex.matchEntire(line)
            if (lineNumber == 1 && "#version" !in text) {
                out += "#line 0 $sourceNumber // $name\n"
            }

            if (importMatch != null) {
                val importName = importMatch.groupValues[1]
                val importLocation = if (':' !in importName) {
                    name.resolveSibling(importName)
                } else {
                    ResourceLocation(importName)
                }

                out += readShader(resourceManager, importLocation, files, stack)
                out += "\n#line $lineNumber $sourceNumber // $name\n"
            } else {
                out += "$line\n"
            }

            if ("#version" in line) {
                out += "#line $lineNumber $sourceNumber // $name\n"
            }
        }

        stack.pop()

        return out
    }

    private fun prependLineNumbers(source: String): String {
        val lineRegex = """^\s*#line\s+(\d+)""".toRegex()
        var lineNumber = 0

        return source.lineSequence().joinToString("\n") { line ->
            lineNumber++
            var lineOut = ""
            lineRegex.matchEntire(line)?.also { match ->
                lineNumber = match.groupValues[1].toInt()
            }
            lineOut += "%4d: %s".format(lineNumber, line)
            lineOut
        }
    }

    private fun compileShader(type: Int, typeName: String, source: String, location: ResourceLocation, files: Map<ResourceLocation, Int>): Int {
        logger.debug("Compiling $typeName shader $location")
        checkVersion(source)
        val shader = GlStateManager.createShader(type)
        if (shader == 0)
            throw ShaderCompilationException("Could not create shader object")
        GlStateManager.shaderSource(shader, source)
        GlStateManager.compileShader(shader)

        val status = GlStateManager.getShader(shader, GL_COMPILE_STATUS)
        val logLength = GlStateManager.getShader(shader, GL_INFO_LOG_LENGTH)
        var log = GlStateManager.getShaderInfoLog(shader, logLength)
        if (status == GL_FALSE) {
            GlStateManager.deleteShader(shader)

            files.forEach { (key, value) ->
                log = log.replace(Regex("\\b$value\\b"), if (key.namespace != location.namespace) "$key" else key.path)
            }

            logger.error("Error compiling $typeName shader $location")
            throw ShaderCompilationException("Error compiling $typeName shader `$location`:\n$log")
        }

        logger.debug("Finished compiling $typeName shader $location")
        return shader
    }

    /**
     * Check the GLSL version directive
     */
    private fun checkVersion(source: String) {
        val match = """#version\s+(\d+)""".toRegex().find(source) ?: return
        val version = match.groupValues[1].toInt()
        if (version > 120) // Apple doesn't support OpenGL 3.0+ properly, so we're stuck with OpenGL 2.1 shaders
            throw ShaderCompilationException("Maximum GLSL version supported by LibrarianLib is 1.20, found `${match.value}`")
    }

    private fun linkProgram(vertexHandle: Int, fragmentHandle: Int): Int {
        logger.debug("Linking shader")
        val program = GlStateManager.createProgram()
        if (program == 0)
            throw ShaderCompilationException("could not create program object")

        // todo set up linking: https://www.khronos.org/opengl/wiki/Shader_Compilation#Before_linking

        if (vertexHandle != 0)
            GlStateManager.attachShader(program, vertexHandle)
        if (fragmentHandle != 0)
            GlStateManager.attachShader(program, fragmentHandle)

        GlStateManager.linkProgram(program)

        val status = GlStateManager.getProgram(program, GL_LINK_STATUS)
        if (status == GL_FALSE) {
            val logLength = GlStateManager.getProgram(program, GL_INFO_LOG_LENGTH)
            val log = GlStateManager.getProgramInfoLog(program, logLength)
            GlStateManager.deleteProgram(program)
            logger.error("Error linking shader")
            throw ShaderCompilationException("Could not link program: $log")
        }

        return program
    }

    private companion object {
        /**
         * So far as I can tell, vanilla doesn't use anything past GL_TEXTURE3, and we don't want to clobber them.
         */
        private const val FIRST_TEXTURE_UNIT = 3

        private var currentlyBound: Shader? = null
        private val allShaders = weakSetOf<Shader>()
        init {
            Client.resourceReloadHandler.register(object: ISimpleReloadListener<Any?> {
                override fun prepare(resourceManager: IResourceManager, profiler: IProfiler): Any? = null

                override fun apply(result: Any?, resourceManager: IResourceManager, profiler: IProfiler) {
                    allShaders.forEach { shader ->
                        shader.compile(resourceManager)
                    }
                }
            })
        }
    }
}
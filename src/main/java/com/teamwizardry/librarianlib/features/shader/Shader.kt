package com.teamwizardry.librarianlib.features.shader

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.shader.uniforms.FloatTypes
import com.teamwizardry.librarianlib.features.shader.uniforms.Uniform
import com.teamwizardry.librarianlib.features.shader.uniforms.UniformType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL20

@SideOnly(Side.CLIENT)
open class Shader(vert: ResourceLocation?, frag: ResourceLocation?) {

    constructor(vert: String?, frag: String?)
            : this(vert?.let { ResourceLocation(currentModId, it) }, frag?.let { ResourceLocation(currentModId, it) })

    val vert: String? = if (vert == null) null else "/assets/${vert.resourceDomain}/${VariantHelper.pathToSnakeCase(vert.resourcePath).removePrefix("/")}"
    val frag: String? = if (frag == null) null else "/assets/${frag.resourceDomain}/${VariantHelper.pathToSnakeCase(frag.resourcePath).removePrefix("/")}"

    var time: FloatTypes.FloatUniform? = null

    var glName = 0
        private set
    private var uniforms: Array<Uniform> = emptyArray()

    fun init(program: Int) {
        glName = program

        val uniformCount = GL20.glGetProgrami(glName, GL20.GL_ACTIVE_UNIFORMS)
        val uniformLength = GL20.glGetProgrami(glName, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH)
        uniforms = Array(uniformCount) {
            val name = GL20.glGetActiveUniform(glName, it, uniformLength)
            val type = GL20.glGetActiveUniformType(glName, it)
            val size = GL20.glGetActiveUniformSize(glName, it)
            val location = GL20.glGetUniformLocation(glName, name)

            makeUniform(name, type, size, location)
        }

        if (uniformCount > 0)
            LibrarianLog.info("Found $uniformCount uniforms. [${uniforms.joinToString(separator = ", ", transform = { it.type.toString() + " `" + it.name + "` @" + it.location })}]")

        time = getUniform<FloatTypes.FloatUniform>("time", true)

        initUniforms()
    }

    open fun initUniforms() {
        //NO-OP
    }

    open fun uniformDefaults() {
        //NO-OP
    }

    fun <T : Uniform> getUniform(name: String): T? {
        return getUniform(name, false)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Uniform> getUniform(name: String, quiet: Boolean): T? {
        uniforms.indices
                .filter { uniforms[it].name == name }
                .forEach {
                    try {
                        return uniforms[it] as T?
                    } catch (e: ClassCastException) {
                        LibrarianLog.debug("Uniform %s was wrong type. (%s)", name, uniforms[it].type.name)
                    }
                }
        if (!quiet) LibrarianLog.debug("Can't find uniform %s", name)
        return null
    }

    private fun makeUniform(name: String, type: Int, size: Int, location: Int): Uniform {
        val enumType = UniformType.getByGlEnum(type)
        return enumType.make(this, name, enumType, size, location)
    }

    companion object {
        val NONE = Shader(null as ResourceLocation?, null)
    }
}

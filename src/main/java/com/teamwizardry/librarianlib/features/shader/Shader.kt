package com.teamwizardry.librarianlib.features.shader

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.allDeclaredFields
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.unmodifiableCopy
import com.teamwizardry.librarianlib.features.shader.uniforms.Uniform
import com.teamwizardry.librarianlib.features.shader.uniforms.UniformType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL20

@SideOnly(Side.CLIENT)
open class Shader(val vert: ResourceLocation?, val frag: ResourceLocation?) {

    constructor(vert: String?, frag: String?)
            : this(vert?.let { ResourceLocation(currentModId, it) }, frag?.let { ResourceLocation(currentModId, it) })

    var glName = 0
        private set
    var handles: Map<String, Uniform> = emptyMap()

    fun loadUniforms(program: Int) {
        glName = program

        handles = javaClass.allDeclaredFields
            .filter { Uniform::class.java.isAssignableFrom(it.type) }
            .associate {
                it.isAccessible = true
                it.name to (it.get(this) as Uniform)
            }.unmodifiableCopy()

        val uniformCount = GL20.glGetProgrami(glName, GL20.GL_ACTIVE_UNIFORMS)
        val uniformLength = GL20.glGetProgrami(glName, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH)

        val specs = (0 until uniformCount).map {
            val name = GL20.glGetActiveUniform(glName, it, uniformLength)
            val type = GL20.glGetActiveUniformType(glName, it)
            val size = GL20.glGetActiveUniformSize(glName, it)
            val location = GL20.glGetUniformLocation(glName, name)
            UniformSpec(name, UniformType.getByGlEnum(type), size, location)
        }

        handles.forEach { (key, value) ->
            value.program = this
            value.name = key
            value.size = 1
            value.location = -1
        }
        val errors = mutableListOf<String>()
        specs.forEach { spec ->
            val handle = handles[spec.name] ?: return@forEach

            if(handle.type != spec.type) {
                errors.add("Type mismatch for `${spec.name}`: Handle type is ${handle.type} but shader type is ${spec.type}")
                return@forEach
            }

            handle.program = this
            handle.name = spec.name
            handle.size = spec.size
            handle.location = spec.location
        }
        val unbound = specs.filter { it.name !in handles }
        val missing = handles.filter { (key, _) -> specs.none { it.name == key } }
        unbound.forEach {
            errors.add("Shader uniform `${it.name}` is missing a handle")
        }
        missing.forEach {
            errors.add("Handle `${it.key}` is missing a shader uniform")
        }
    }

    private data class UniformSpec(val name: String, val type: UniformType, val size: Int, val location: Int)

    companion object {
        val NONE = Shader(null as ResourceLocation?, null)
    }
}

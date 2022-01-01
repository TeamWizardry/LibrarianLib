package com.teamwizardry.librarianlib.albedo.buffer

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.AlbedoTypeConversion
import com.teamwizardry.librarianlib.albedo.base.buffer.BasicBufferImpl
import com.teamwizardry.librarianlib.albedo.shader.StandardUniforms
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.AbstractUniform
import com.teamwizardry.librarianlib.albedo.shader.uniform.SamplerArrayUniform
import com.teamwizardry.librarianlib.albedo.shader.uniform.SamplerUniform
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.GlResourceGc
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
import net.minecraft.client.render.BufferRenderer
import net.minecraft.util.profiler.Profiler
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import kotlin.math.min

public abstract class RenderBuffer(private val vbo: VertexBuffer, vararg supportedPrimitives: Primitive) {

    private val supportedPrimitives = supportedPrimitives.toSet()
    private var vao: Int by GlResourceGc.track(this, glGenVertexArrays()) { glDeleteVertexArrays(it) }
    private var byteBuffer: ByteBuffer by GlResourceGc.track(this, MemoryUtil.memAlloc(1)) { MemoryUtil.memFree(it) }

    private val attributes = mutableListOf<VertexLayoutElement>()
    private val uniforms = mutableListOf<AbstractUniform>()
    private var linkedUniforms: List<Uniform>? = null
    private var shader: Shader? = null
    private val boundTextureUnits = Long2IntOpenHashMap(32)

    private var size: Int = 64
    private var count: Int = 0
    private var stride: Int = 0

    public fun endVertex() {
        count++
        ensureCapacity()
    }

    /**
     * Duplicate the last vertex
     */
    public fun dupVertex() {
        if(count == 0)
            throw IllegalStateException("Can't duplicate last vertex of empty buffer")
        byteBuffer.put(count * stride, byteBuffer, (count - 1) * stride, stride)
        endVertex()
    }

    public fun clear() {
        count = 0
    }
    public fun draw(primitive: Primitive) {
        draw(primitive, null)
    }
    public fun draw(primitive: Primitive, profiler: Profiler?) {
        if(supportedPrimitives.isNotEmpty() && primitive !in supportedPrimitives) {
            throw IllegalArgumentException(
                "Unsupported primitive $primitive. This render buffer only supports these primitives: " +
                        "[${supportedPrimitives.joinToString()}]"
            )
        }
        BufferRenderer.unbindAll() // Tell Mojang to wrap up their rendering
        if(this.shader == null)
            throw IllegalStateException("RenderBuffer not bound to a shader")
        profiler?.push("RenderBuffer.draw")
        setupState()
        useProgram()
        uploadUniforms()
        drawVertices(primitive, profiler)
        teardownState()
        profiler?.pop()
    }

    protected fun start(attribute: VertexLayoutElement) {
        byteBuffer.position(count * stride + attribute.offset)
    }

    protected fun putDouble(value: Double) {
        byteBuffer.putDouble(value)
    }

    protected fun putFloat(value: Float) {
        byteBuffer.putFloat(value)
    }

    protected fun putInt(value: Int) {
        byteBuffer.putInt(value)
    }

    protected fun putShort(value: Short) {
        byteBuffer.putShort(value)
    }

    protected fun putByte(value: Int) {
        byteBuffer.put(value.toByte())
    }

    protected fun putHalfFloat(value: Float) {
        byteBuffer.putShort(AlbedoTypeConversion.GL_HALF_FLOAT(value).toShort())
    }

    protected fun putFixedFloat(value: Float) {
        byteBuffer.putInt(AlbedoTypeConversion.GL_FIXED(value))
    }

    /**
     * Called immediately before drawing. One of its primary roles is setting standard uniform values using
     * [StandardUniforms].
     */
    protected open fun setupState() {}

    /**
     * Called immediately after drawing.
     */
    protected open fun teardownState() {}

    public fun bind(shader: Shader) {
        this.shader = shader
        linkedUniforms = shader.bindUniforms(uniforms)
        shader.bindAttributes(attributes)

        useProgram()
        glBindBuffer(GL_ARRAY_BUFFER, vbo.vbo)
        glBindVertexArray(vao)
        for (attribute in attributes) {
            attribute.setupVertexAttribPointer(stride)
        }
        ensureCapacity()
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    protected fun <T : AbstractUniform> add(uniform: T): T {
        uniforms.add(uniform)
        return uniform
    }

    protected fun add(attribute: VertexLayoutElement): VertexLayoutElement {
        attribute.offset = stride
        stride += attribute.width
        ensureCapacity()
        attributes.add(attribute)
        return attribute
    }

    @JvmSynthetic
    protected operator fun <T : AbstractUniform> T.unaryPlus(): T = add(this)

    @JvmSynthetic
    protected operator fun VertexLayoutElement.unaryPlus(): VertexLayoutElement = add(this)

    /**
     * Provides public access to the internal configuration and buffer manipulation functions. Designed for code
     * requiring mixin-style access (e.g. [BasicBufferImpl])
     */
    public interface InternalAccess {
        public fun add(uniform: Uniform): Uniform
        public fun add(attribute: VertexLayoutElement): VertexLayoutElement
        public fun start(attribute: VertexLayoutElement)
        public fun bind(shader: Shader)

        public fun putDouble(value: Double)
        public fun putFloat(value: Float)
        public fun putInt(value: Int)
        public fun putShort(value: Short)
        public fun putByte(value: Int)
        public fun putHalfFloat(value: Float)
        public fun putFixedFloat(value: Float)
    }

    /**
     * Provides public access to the internal configuration and buffer manipulation functions. Designed for code
     * requiring mixin-style access (e.g. [BasicBufferImpl])
     */
    protected val internalAccess: InternalAccess = object : InternalAccess {
        override fun add(uniform: Uniform): Uniform = this@RenderBuffer.add(uniform)
        override fun add(attribute: VertexLayoutElement): VertexLayoutElement = this@RenderBuffer.add(attribute)
        override fun bind(shader: Shader): Unit = this@RenderBuffer.bind(shader)

        override fun start(attribute: VertexLayoutElement): Unit = this@RenderBuffer.start(attribute)
        override fun putDouble(value: Double): Unit = this@RenderBuffer.putDouble(value)
        override fun putFloat(value: Float): Unit = this@RenderBuffer.putFloat(value)
        override fun putInt(value: Int): Unit = this@RenderBuffer.putInt(value)
        override fun putShort(value: Short): Unit = this@RenderBuffer.putShort(value)
        override fun putByte(value: Int): Unit = this@RenderBuffer.putByte(value)
        override fun putHalfFloat(value: Float): Unit = this@RenderBuffer.putHalfFloat(value)
        override fun putFixedFloat(value: Float): Unit = this@RenderBuffer.putFixedFloat(value)
    }

    private fun useProgram() {
        val shader = this.shader ?: return
        glUseProgram(shader.glProgram)
    }

    private var currentElementBuffer: Int = -1

    private fun uploadUniforms() {
        var nextUnit = 0
        boundTextureUnits.clear()
        linkedUniforms?.forEach {
            when (it) {
                is SamplerUniform -> {
                    val packed = packTextureBinding(it.textureTarget, it.get())
                    if(boundTextureUnits.containsKey(packed)) {
                        it.textureUnit = boundTextureUnits[packed]
                    } else {
                        it.textureUnit = nextUnit
                        boundTextureUnits[packed] = nextUnit++
                    }
                }
                is SamplerArrayUniform -> {
                    for (i in 0 until min(it.length, it.trueLength)) {
                        val packed = packTextureBinding(it.textureTarget, it[i])
                        if(boundTextureUnits.containsKey(packed)) {
                            it.textureUnits[i] = boundTextureUnits[packed]
                        } else {
                            it.textureUnits[i] = nextUnit
                            boundTextureUnits[packed] = nextUnit++
                        }
                    }
                }
                else -> {
                }
            }
        }
        boundTextureUnits.long2IntEntrySet().fastForEach { entry ->
            bindTexture(unpackTarget(entry.longKey), unpackTexture(entry.longKey), entry.intValue)
        }
        RenderSystem.activeTexture(GL_TEXTURE0)

        linkedUniforms?.forEach {
            it.push()
        }
    }

    private fun drawVertices(primitive: Primitive, profiler: Profiler?) {
        profiler?.push("Upload VBO")
        byteBuffer.position(0)
        byteBuffer.limit(count * stride)
        vbo.upload(0, byteBuffer)
        byteBuffer.limit(byteBuffer.capacity())
        profiler?.swap("glDraw*")
        glBindVertexArray(vao)
        val indexBuffer = primitive.indexBuffer(primitive.elementCount(count))
        if (indexBuffer != null) {
            if (currentElementBuffer != indexBuffer.id) {
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.id)
                currentElementBuffer = indexBuffer.id
            }
            val indexType = indexBuffer.elementFormat.count // this is actually a gl enum. the mappings are bad.
            glDrawElements(primitive.resultType, primitive.elementCount(count), indexType, 0L)
        } else {
            glDrawArrays(primitive.resultType, 0, count)
        }
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        count = 0
        glUseProgram(0)
        profiler?.pop()
    }

    public open fun delete() {
        glDeleteVertexArrays(vao)
        vao = 0
    }

    private fun packTextureBinding(target: Int, texture: Int): Long {
        return ((target.toULong() shl 32) or texture.toULong()).toLong()
    }

    private fun unpackTexture(packed: Long): Int {
        return packed.toInt()
    }

    private fun unpackTarget(packed: Long): Int {
        return (packed ushr 32).toInt()
    }

    private fun bindTexture(target: Int, texture: Int, unit: Int) {
        val glUnit = GL_TEXTURE0 + unit
        if (unit < GlStateManager.TEXTURE_COUNT) {
            GlStateManager._activeTexture(glUnit)
            GlStateManager._bindTexture(-1)
        }
        glActiveTexture(glUnit)
        glBindTexture(target, texture)
        GlStateManager._activeTexture(GL_TEXTURE0)
    }

    private fun ensureCapacity() {
        if (count >= size) {
            size += size / 2
        }
        val bytes = size * stride
        if (byteBuffer.capacity() < bytes) {
            byteBuffer = MemoryUtil.memRealloc(byteBuffer, bytes)
        }
    }

}
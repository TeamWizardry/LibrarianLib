package com.teamwizardry.librarianlib.albedo.buffer

import com.teamwizardry.librarianlib.albedo.ShaderBinding
import com.teamwizardry.librarianlib.albedo.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.core.util.GlResourceGc
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

public abstract class RenderBuffer : ShaderBinding() {
    private var vbo = VertexBuffer()
    private var vao: Int by GlResourceGc.track(this, glGenVertexArrays()) { glDeleteVertexArrays(it) }

    public var byteBuffer: ByteBuffer by GlResourceGc.track(this, MemoryUtil.memAlloc(1)) { MemoryUtil.memFree(it) }
        private set
    private var size: Int = 64
    private var count: Int = 0

    protected fun seek(attribute: VertexLayoutElement) {
        byteBuffer.position(count * stride + attribute.offset)
    }

    protected fun putFloat(value: Float) {
        byteBuffer.putFloat(value)
    }

    protected fun putByte(value: Int) {
        byteBuffer.put(value.toByte())
    }

    public fun setupVAO() {
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

    public fun endVertex() {
        count++
        ensureCapacity()
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

    private var currentElementBuffer: Int = -1

    public fun draw(primitive: Primitive) {
        useProgram()
        pushUniforms()
        byteBuffer.position(0)
        byteBuffer.limit(count * stride)
        vbo.upload(0, byteBuffer)
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
        cleanup()
        byteBuffer.limit(byteBuffer.capacity())
    }

    public open fun delete() {
        glDeleteVertexArrays(vao)
        vao = 0
    }
}
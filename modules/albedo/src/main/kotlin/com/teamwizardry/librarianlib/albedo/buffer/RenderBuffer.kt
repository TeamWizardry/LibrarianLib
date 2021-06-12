package com.teamwizardry.librarianlib.albedo.buffer

import com.teamwizardry.librarianlib.albedo.ShaderBinding
import com.teamwizardry.librarianlib.albedo.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.core.util.GlResourceGc
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15.glDeleteBuffers
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

public abstract class RenderBuffer : ShaderBinding() {
    private var vbo: Int by GlResourceGc.track(this, glGenBuffers()) { glDeleteBuffers(it) }
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
        glBindVertexArray(vao)
        for(attribute in attributes) {
            attribute.setupVertexAttribPointer(stride)
        }
        ensureCapacity()
        glBindVertexArray(0)
    }

    public fun endVertex() {
        count++
        ensureCapacity()
    }

    private fun ensureCapacity() {
        if(count >= size) {
            size += size / 2
        }
        val bytes = size * stride
        if(byteBuffer.capacity() < bytes) {
            byteBuffer = MemoryUtil.memRealloc(byteBuffer, bytes)
        }
    }

    public fun draw(type: Int) {
        useProgram()
        pushUniforms()
        glBindVertexArray(vao)
        GL15.glBufferData(vbo, byteBuffer.slice(0, count * stride), GL_DYNAMIC_DRAW)
        glDrawArrays(type, 0, count)
        count = 0
        unbind()
    }

    public open fun delete() {
        glDeleteBuffers(vbo)
        vbo = 0
        glDeleteVertexArrays(vao)
        vao = 0
    }
}
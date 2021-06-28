package com.teamwizardry.librarianlib.albedo.buffer

import com.teamwizardry.librarianlib.core.util.GlResourceGc
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL15.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

/**
 * An OpenGL Vertex Buffer Object
 */
public class VertexBuffer {
    public var usage: Int = GL_DYNAMIC_DRAW
    public var vbo: Int by GlResourceGc.track(this, glGenBuffers()) { glDeleteBuffers(it) }
    private var size: Int = 0

    public fun upload(start: Int, data: ByteBuffer) {
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        if(size < start + data.remaining()) {
            size = MathHelper.smallestEncompassingPowerOfTwo(start + data.remaining())
        }
        // Reallocate the buffer. If the size has not changed this has the effect of "orphaning" the buffer, which can
        // improve performance: https://www.khronos.org/opengl/wiki/Buffer_Object_Streaming#Buffer_re-specification
        glBufferData(GL_ARRAY_BUFFER, size.toLong(), usage)
        glBufferSubData(GL_ARRAY_BUFFER, start.toLong(), data)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    public fun delete() {
        glDeleteBuffers(vbo)
        vbo = 0
    }

    public companion object {
        /**
         * A shared vertex buffer for use in immediate mode rendering.
         */
        @JvmStatic
        public val SHARED: VertexBuffer = VertexBuffer()
    }
}
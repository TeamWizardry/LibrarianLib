package com.teamwizardry.librarianlib.albedo.buffer

import com.teamwizardry.librarianlib.core.util.GlResourceGc
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

//public var glBuffer: Int by GlResourceGc.track(this, GL15.glGenBuffers()) { GL15.glDeleteBuffers(it) }
//    private set
/**
 * A buffer for vertex data
 */
public class VertexBuffer {
    public var byteBuffer: ByteBuffer by GlResourceGc.track(this, MemoryUtil.memAlloc(0)) { MemoryUtil.memFree(it) }
        private set

    public var position: Int
        get() = byteBuffer.position()
        set(value) {
            byteBuffer.position(value)
        }

    public fun putFloat(value: Float): VertexBuffer = build { putFloat(value) }
    public fun putDouble(value: Double): VertexBuffer = build { putDouble(value) }
    public fun putFixed(value: Double): VertexBuffer = build { putDouble(value) }

    private inline fun build(block: ByteBuffer.() -> Unit): VertexBuffer {
        byteBuffer.block()
        return this
    }
}
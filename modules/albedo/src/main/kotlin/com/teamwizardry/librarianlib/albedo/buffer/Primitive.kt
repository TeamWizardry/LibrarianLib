package com.teamwizardry.librarianlib.albedo.buffer

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.VertexFormat
import org.lwjgl.opengl.GL11.*

public enum class Primitive(
    public val resultType: Int
) {
    TRIANGLES(GL_TRIANGLES),
    QUADS(GL_TRIANGLES),
    LINES(GL_LINES),
    LINE_STRIP(GL_LINE_STRIP),
    LINE_LOOP(GL_LINE_LOOP),
    TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(GL_TRIANGLE_FAN);

    public fun indexBuffer(vertexCount: Int): RenderSystem.IndexBuffer? {
        return when(this) {
            QUADS -> RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS, vertexCount)
            else -> null
        }
    }

    public fun elementCount(vertexCount: Int): Int {
        return if(this == QUADS)
            vertexCount / 4 * 6
        else
            vertexCount
    }

}

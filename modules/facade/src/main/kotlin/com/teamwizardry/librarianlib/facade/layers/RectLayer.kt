package com.teamwizardry.librarianlib.facade.components

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.facade.component.GuiLayer
import com.teamwizardry.librarianlib.facade.component.GuiDrawContext
import com.teamwizardry.librarianlib.facade.value.IMValue
import net.minecraft.client.renderer.IRenderTypeBuffer
import org.lwjgl.opengl.GL11
import java.awt.Color

class RectLayer(color: Color, x: Int, y: Int, width: Int, height: Int): GuiLayer(x, y, width, height) {
    constructor(color: Color, x: Int, y: Int): this(color, x, y, 0, 0)
    constructor(x: Int, y: Int): this(Color.white, x, y)
    constructor(color: Color): this(color, 0, 0)
    constructor(): this(Color.white)

    val color_im: IMValue<Color> = IMValue(color)
    var color: Color by color_im

    override fun draw(context: GuiDrawContext) {
        val minX = 0.0
        val minY = 0.0
        val maxX = size.xi.toDouble()
        val maxY = size.yi.toDouble()

        val c = color

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.pos2d(context.matrix, minX, maxY).color(c).endVertex()
        vb.pos2d(context.matrix, maxX, maxY).color(c).endVertex()
        vb.pos2d(context.matrix, maxX, minY).color(c).endVertex()
        vb.pos2d(context.matrix, minX, minY).color(c).endVertex()

        buffer.finish()
    }

    companion object {
        private val renderType = SimpleRenderTypes.flat(GL11.GL_QUADS)
    }
}
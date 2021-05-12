package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers.flatQuads
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.value.IMValue
import net.minecraft.client.render.VertexConsumerProvider
import java.awt.Color

public class RectLayer(color: Color, x: Int, y: Int, width: Int, height: Int): GuiLayer(x, y, width, height) {
    public constructor(color: Color, x: Int, y: Int): this(color, x, y, 0, 0)
    public constructor(x: Int, y: Int): this(Color.white, x, y)
    public constructor(color: Color): this(color, 0, 0)
    public constructor(): this(Color.white)

    public val color_im: IMValue<Color> = imValue(color)
    public var color: Color by color_im

    override fun draw(context: GuiDrawContext) {
        val minX = 0.0
        val minY = 0.0
        val maxX = size.xi.toDouble()
        val maxY = size.yi.toDouble()

        val c = color

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(flatQuads)

        vb.pos2d(context.transform, minX, maxY).color(c).endVertex()
        vb.pos2d(context.transform, maxX, maxY).color(c).endVertex()
        vb.pos2d(context.transform, maxX, minY).color(c).endVertex()
        vb.pos2d(context.transform, minX, minY).color(c).endVertex()

        buffer.draw()
    }
}
package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValue
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

        val buffer = FlatColorRenderBuffer.SHARED

        buffer.pos(context.transform, minX, maxY, 0).color(c).endVertex()
        buffer.pos(context.transform, maxX, maxY, 0).color(c).endVertex()
        buffer.pos(context.transform, maxX, minY, 0).color(c).endVertex()
        buffer.pos(context.transform, minX, minY, 0).color(c).endVertex()

        buffer.draw(Primitive.QUADS)
    }
}
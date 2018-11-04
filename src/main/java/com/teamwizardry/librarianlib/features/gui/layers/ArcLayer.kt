package com.teamwizardry.librarianlib.features.gui.layers

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.value.IMValue
import com.teamwizardry.librarianlib.features.gui.value.IMValueDouble
import com.teamwizardry.librarianlib.features.gui.value.IMValueInt
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.Math.PI
import kotlin.math.max
import kotlin.math.min

class ArcLayer(color: Color = Color.white, posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    init {
        anchor = vec(0.5, 0.5)
    }
    val color_im: IMValue<Color> = IMValue(color)
    var color: Color by color_im

    val startAngle_im: IMValueDouble = IMValueDouble(0.0)
    var startAngle: Double by startAngle_im
    val endAngle_im: IMValueDouble = IMValueDouble(2*PI)
    var endAngle: Double by endAngle_im

    val points_im: IMValueInt = IMValueInt(25)
    var points: Int by points_im

    override fun draw(partialTicks: Float) {
        val startWrap = startAngle % (2*PI)
        val endWrap = endAngle % (2*PI)
        val start = min(startWrap, endWrap)
        val end = max(startWrap, endWrap)
        val rX = size.x/2
        val rY = size.y/2

        val steps = max(2, points)-1

        val c = color

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        GlStateManager.disableTexture2D()
        GlStateManager.disableCull()
        GlStateManager.enableBlend()

        GlStateManager.color(c.red / 255f, c.green / 255f, c.blue / 255f, c.alpha / 255f)

        vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION)
        vb.setTranslation(size.x/2, size.y/2, 0.0)
        vb.pos(0.0, 0.0, 0.0).endVertex()
        for(i in 0..steps) {
            val a = start + (end-start)*i/steps
            val cos = Math.cos(a)
            val sin = Math.sin(a)
            vb.pos(rX*sin, rY*-cos, 0.0).endVertex()
        }
        vb.setTranslation(0.0, 0.0, 0.0)
        tessellator.draw()

        GlStateManager.enableCull()
        GlStateManager.enableTexture2D()
    }
}
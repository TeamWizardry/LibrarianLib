package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.Option
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class ComponentRect(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent<ComponentRect>(posX, posY, width, height) {

    val color = Option<ComponentRect, Color>(Color.WHITE)

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val minX = pos.xi.toDouble()
        val minY = pos.yi.toDouble()
        val maxX = pos.xi + size.xi.toDouble()
        val maxY = pos.yi + size.yi.toDouble()

        val c = color.getValue(this)

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        GlStateManager.pushAttrib()
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(c.red / 255f, c.green / 255f, c.blue / 255f, c.alpha / 255f)

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        vb.pos(minX, minY, 0.0).endVertex()
        vb.pos(minX, maxY, 0.0).endVertex()
        vb.pos(maxX, maxY, 0.0).endVertex()
        vb.pos(maxX, minY, 0.0).endVertex()
        tessellator.draw()

        GlStateManager.popAttrib()
    }
}

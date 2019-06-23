package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
/**
 * ## Facade equivalent: [RectLayer][com.teamwizardry.librarianlib.features.facade.layers.RectLayer]
 */
@Deprecated("As of version 4.20 this has been superseded by Facade")
class ComponentRect(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    val color = Option<ComponentRect, Color>(Color.WHITE)

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val minX = 0.0
        val minY = 0.0
        val maxX = size.xi.toDouble()
        val maxY = size.yi.toDouble()

        val c = color.getValue(this)

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        GlStateManager.disableTexture2D()

        GlStateManager.enableBlend()
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(c.red / 255f, c.green / 255f, c.blue / 255f, c.alpha / 255f)

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        vb.pos(minX, minY, 0.0).endVertex()
        vb.pos(minX, maxY, 0.0).endVertex()
        vb.pos(maxX, maxY, 0.0).endVertex()
        vb.pos(maxX, minY, 0.0).endVertex()
        tessellator.draw()

        GlStateManager.enableTexture2D()
    }
}

package com.teamwizardry.librarianlib.features.facade.components

import com.teamwizardry.librarianlib.features.facade.value.IMValue
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class ComponentRect(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    val color_im: IMValue<Color> = IMValue(Color.WHITE)
    var color: Color by color_im

    override fun draw(partialTicks: Float) {
        val minX = 0.0
        val minY = 0.0
        val maxX = size.xi.toDouble()
        val maxY = size.yi.toDouble()

        val c = color

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

package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.facade.value.GuiAnimator
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestCoordinateConversion : GuiBase() {
    init {
        main.size = vec(300, 100)

        val background = RectLayer(Color.WHITE, 0, 0, 300, 100)

        val targetContainer = RectLayer(Color.GREEN, 0, 0, 40, 40).componentWrapper()
        val targetContainerIndicator = RectLayer(Color.GREEN
            .let { Color(it.red, it.green, it.blue, 128) }, 0, 0, 40, 40)
        val target = RectLayer(Color.GREEN.darker(), 20, 20, 40, 40).componentWrapper()
        val targetIndicator = RectLayer(Color.GREEN.darker()
            .let { Color(it.red, it.green, it.blue, 128) }, 20, 20, 40, 40)
        targetContainer.add(targetIndicator, target)

        val sourceContainer = RectLayer(Color.RED, 260, 0, 40, 40).componentWrapper()
        val sourceContainerIndicator = RectLayer(Color.RED.darker()
            .let { Color(it.red, it.green, it.blue, 128) }, 260, 0, 40, 40)
        val source = RectLayer(Color.RED.darker(), -20, 20, 40, 40).componentWrapper()
        val sourceIndicator = RectLayer(Color.RED.darker()
            .let { Color(it.red, it.green, it.blue, 128) }, -20, 20, 40, 40)
        sourceContainer.add(sourceIndicator, source)

        if(GuiScreen.isShiftKeyDown()) {
            val anim = GuiAnimator.animate(60f) {
                targetContainer.rotation = Math.toRadians(15.0)
                target.rotation = Math.toRadians(-7.0)
                target.scale2d = vec(0.75, 0.25)
                target.anchor = vec(0.25, 0.75)
                target.contentsOffset = vec(5, 2)

                sourceContainer.rotation = Math.toRadians(-8.0)
                sourceContainer.scale2d = vec(0.8, 0.2)
                source.rotation = Math.toRadians(4.0)
                source.scale2d = vec(0.5, 0.2)
                source.anchor = vec(0.1, 1)
                source.contentsOffset = vec(5, -10)
            }
            anim.shouldReverse = true
            anim.repeatCount = -1
            main.add(anim)
        } else {
            source.anchor = vec(0.5, 0.5)
            val anim = GuiAnimator.animate(60f) {
                source.scale = 0.5
            }
            anim.shouldReverse = true
            anim.repeatCount = -1
            main.add(anim)
        }


        val sourceMouseIndicator = object: GuiComponent(0, 0, 0, 0) {
            override fun draw(partialTicks: Float) {
                val tessellator = Tessellator.getInstance()
                val vb = tessellator.buffer

                GlStateManager.disableTexture2D()

                GlStateManager.enableBlend()
                val c = Color(1f, 0.2f, 0.2f)
                GlStateManager.color(c.red / 255f, c.green / 255f, c.blue / 255f, c.alpha / 255f)

                vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
                vb.pos(mousePos.x-10, mousePos.y, 1.0).endVertex()
                vb.pos(mousePos.x+10, mousePos.y, 1.0).endVertex()
                vb.pos(mousePos.x, mousePos.y-10, 1.0).endVertex()
                vb.pos(mousePos.x, mousePos.y+10, 1.0).endVertex()
                tessellator.draw()

                GlStateManager.enableTexture2D()
            }
        }

        val targetMouseIndicator = object: GuiComponent(0, 0, 0, 0) {
            override fun draw(partialTicks: Float) {
                val tessellator = Tessellator.getInstance()
                val vb = tessellator.buffer

                GlStateManager.disableTexture2D()

                GlStateManager.enableBlend()
                var c = Color(0.1f, 0.1f, 1f)
                GlStateManager.color(c.red / 255f, c.green / 255f, c.blue / 255f, c.alpha / 255f)

                vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
                vb.pos(mousePos.x-10, mousePos.y, 1.0).endVertex()
                vb.pos(mousePos.x+10, mousePos.y, 1.0).endVertex()
                vb.pos(mousePos.x, mousePos.y-10, 1.0).endVertex()
                vb.pos(mousePos.x, mousePos.y+10, 1.0).endVertex()
                tessellator.draw()

                val otherPos = source.convertPointTo(source.mousePos, this)

                c = Color(1f, 0.1f, 1f)
                GlStateManager.color(c.red / 255f, c.green / 255f, c.blue / 255f, c.alpha / 255f)

                vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
                vb.pos(mousePos.x, mousePos.y, 1.0).endVertex()
                vb.pos(otherPos.x, otherPos.y, 1.0).endVertex()
                vb.pos(otherPos.x-10, otherPos.y-10, 1.0).endVertex()
                vb.pos(otherPos.x+10, otherPos.y+10, 1.0).endVertex()
                vb.pos(otherPos.x+10, otherPos.y-10, 1.0).endVertex()
                vb.pos(otherPos.x-10, otherPos.y+10, 1.0).endVertex()
                tessellator.draw()

                GlStateManager.enableTexture2D()
            }
        }

        source.add(sourceMouseIndicator)
        target.add(targetMouseIndicator)

        main.add(background, sourceContainerIndicator, sourceContainer, targetContainerIndicator, targetContainer)
    }
}

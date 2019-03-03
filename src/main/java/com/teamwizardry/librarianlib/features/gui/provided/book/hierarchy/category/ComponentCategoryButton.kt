package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.category

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentAnimatableVoid
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextFormatting
import org.lwjgl.opengl.GL11

class ComponentCategoryButton(posX: Int, posY: Int, width: Int, height: Int, book: IBookGui, category: Category) : GuiComponent(posX, posY, width, height) {

    init {

        val icon = category.icon

        BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) { book.placeInFocus(category) }

        // ------- BUTTON RENDERING AND ANIMATION ------- //
        val iconMask = IBookGui.getRendererFor(icon, vec(24.0, 24.0), true)

        BUS.hook(GuiLayerEvents.PreDrawEvent::class.java) {
            GlStateManager.color(0f, 0f, 0f)
            iconMask()
            GlStateManager.color(1f, 1f, 1f)
        }

        tooltip_im {
            val list = mutableListOf<String>()
            category.title?.add(list)
            category.desc?.addDynamic(list)

            for (i in 1 until list.size)
                list[i] = TextFormatting.GRAY.toString() + list[i]
            list
        }

        val circleWipe = ComponentAnimatableVoid(0, 0, width, height)
        circleWipe.translateZ = 100.0
        add(circleWipe)

        circleWipe.clipToBounds = true
        circleWipe.customClipping = {
            GlStateManager.disableTexture2D()
            GlStateManager.disableCull()
            val tessellator = Tessellator.getInstance()
            val buffer = tessellator.buffer
            buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)
            val centerX = (width / 2).toFloat()
            val centerY = (height / 2).toFloat()
            buffer.pos(centerX.toDouble(), centerY.toDouble(), 100.0).color(0f, 1f, 1f, 1f).endVertex()

            for (i in 0..10) {
                val angle = (i.toDouble() * Math.PI * 2.0 / 10).toFloat()
                val x = (centerX + MathHelper.cos(angle) * circleWipe.animX).toFloat()
                val y = (centerY + MathHelper.sin(angle) * circleWipe.animX).toFloat()

                buffer.pos(x.toDouble(), y.toDouble(), 100.0).color(0f, 1f, 1f, 1f).endVertex()
            }
            tessellator.draw()
        }

        val radius = 16.0

        circleWipe.BUS.hook(GuiComponentEvents.MouseMoveInEvent::class.java) {

            val mouseInAnim = BasicAnimation(circleWipe, "animX")
            mouseInAnim.duration = 20f
            mouseInAnim.easing = Easing.easeOutQuint
            mouseInAnim.to = radius
            circleWipe.add(mouseInAnim)
        }

        circleWipe.BUS.hook(GuiComponentEvents.MouseMoveOutEvent::class.java) {

            val mouseOutAnim = BasicAnimation(circleWipe, "animX")
            mouseOutAnim.duration = 20f
            mouseOutAnim.easing = Easing.easeOutQuint
            mouseOutAnim.to = 0
            circleWipe.add(mouseOutAnim)
        }

        val wipeColor = category.color

        circleWipe.BUS.hook(GuiLayerEvents.PreDrawEvent::class.java) {
            GlStateManager.color(wipeColor.red / 255f, wipeColor.green / 255f, wipeColor.blue / 255f)
            GlStateManager.enableAlpha()
            GlStateManager.disableCull()
            iconMask()
            GlStateManager.enableCull()
        }

        // ------- BUTTON RENDERING AND ANIMATION ------- //
    }
}

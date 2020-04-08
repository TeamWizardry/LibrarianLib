package com.teamwizardry.librarianlib.gui

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiDrawContext
import com.teamwizardry.librarianlib.gui.provided.SafetyNetErrorScreen
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.gui.IGuiEventListener
import net.minecraft.client.gui.IRenderable
import net.minecraft.client.gui.screen.Screen

open class FacadeWidget(
    private val screen: Screen
): IRenderable, IGuiEventListener {
    val root = GuiComponent()

    override fun mouseMoved(xPos: Double, yPos: Double) {
        super.mouseMoved(xPos, yPos)
    }

    override fun mouseClicked(xPos: Double, yPos: Double, button: Int): Boolean {
        return super.mouseClicked(xPos, yPos, button)
    }

    override fun isMouseOver(xPos: Double, yPos: Double): Boolean {
        return super.isMouseOver(xPos, yPos)
    }

    override fun mouseReleased(xPos: Double, yPos: Double, button: Int): Boolean {
        return super.mouseReleased(xPos, yPos, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        return super.mouseScrolled(mouseX, mouseY, delta)
    }

    override fun mouseDragged(xPos: Double, yPos: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(xPos, yPos, button, deltaX, deltaY)
    }

    override fun changeFocus(reverse: Boolean): Boolean {
        return super.changeFocus(reverse)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return super.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun charTyped(codepoint: Char, modifiers: Int): Boolean {
        return super.charTyped(codepoint, modifiers)
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        try {
            root.pos = vec(0, 0)
            root.scale = Client.guiScaleFactor
            root.size = vec(Client.window.scaledWidth, Client.window.scaledHeight)

//            sendInputs()
//            updateComponents()
//            updateLayout()
//            drawComponents()

            val context = GuiDrawContext(Matrix3dStack(), false)
            root.renderLayer(context)
        } catch (e: Exception) {
            logger.error("Error in GUI:", e)
            Client.displayGuiScreen(SafetyNetErrorScreen(e))
        }
//        StencilUtil.clear()
//
//        try {
//            sortChildren()
//
//            runLayoutIfNeeded()
//            callPreFrame()
//
//            topMouseHit = null
//            updateMouse(mousePos)
//            updateHits(this, 0.0)
//            mouseOverComponents.clear()
//            propagateHits()
//
//            val tooltip: GuiLayer? = mouseOverComponents
//                .mapNotNull { component ->
//                    component.tooltipLayer?.let { tt -> component to tt }
//                }
//                .maxBy { it.first.mouseHit?.zIndex ?: Double.NEGATIVE_INFINITY }
//                ?.second
//            if(tooltip != currentTooltip) {
//                currentTooltip?.removeFromParent()
//                currentTooltip = tooltip
//                tooltip?.also { this.add(it) }
//            }
//
//            if(enableNativeCursor) {
////                Mouse.setNativeCursor(topMouseHit?.cursor?.lwjglCursor)
//            }
//            val context = GuiDrawContext(Matrix3dStack())
//            renderLayer(context)
//        } catch(e: Exception) {
//            if(!safetyNet) throw e
//
////            Mouse.setNativeCursor(null)
//            val tess = Tessellator.getInstance()
//            try {
//                tess.buffer.finishDrawing()
//            } catch(e2: IllegalStateException) {
//                // the buffer wasn't mid-draw
//            }
//
//            closeGui(e)
//        }
//
//        GL11.glDisable(GL11.GL_STENCIL_TEST)
    }

}

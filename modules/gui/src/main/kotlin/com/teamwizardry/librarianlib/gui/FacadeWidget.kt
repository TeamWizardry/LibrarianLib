package com.teamwizardry.librarianlib.gui

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.gui.component.GuiDrawContext
import com.teamwizardry.librarianlib.gui.provided.SafetyNetErrorScreen
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.gui.screen.Screen
import org.lwjgl.glfw.GLFW

/**
 *
 */
open class FacadeWidget(
    private val screen: Screen
) {
    val root = GuiLayer()
    val main = GuiLayer()

    /**
     * We keep track of the mouse position ourselves both so we can provide deltas for move events and so we can provide
     * subpixel mouse positions in [render]
     */
    private var mouseX = 0.0
    private var mouseY = 0.0

    fun mouseMoved(xPos: Double, yPos: Double) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiLayerEvents.MouseMove(vec(xPos, yPos), vec(mouseX, mouseY)))
        mouseX = xPos
        mouseY = yPos
    }

    fun mouseClicked(xPos: Double, yPos: Double, button: Int) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiLayerEvents.MouseDown(vec(xPos, yPos), button))
    }

    fun isMouseOver(xPos: Double, yPos: Double) {
    }

    fun mouseReleased(xPos: Double, yPos: Double, button: Int) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiLayerEvents.MouseUp(vec(xPos, yPos), button))
    }

    fun mouseScrolled(xPos: Double, yPos: Double, delta: Double) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiLayerEvents.MouseScroll(vec(xPos, yPos), vec(0.0, delta)))
    }

    fun mouseDragged(xPos: Double, yPos: Double, button: Int, deltaX: Double, deltaY: Double) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiLayerEvents.MouseDrag(vec(xPos, yPos), vec(xPos - deltaX, yPos - deltaY), button))
    }

    private fun computeMouseOver(xPos: Double, yPos: Double) {
        val mouseOver = root.computeMouseInfo(vec(xPos, yPos), Matrix3dStack())
        generateSequence(mouseOver) { it.parent }.forEach {
            it.mouseOver = true
        }
    }

    fun changeFocus(reverse: Boolean) {
        // todo
    }

    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            screen.onClose()
        }
        root.triggerEvent(GuiLayerEvents.KeyDown(keyCode, scanCode, modifiers))
    }

    fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        root.triggerEvent(GuiLayerEvents.KeyUp(keyCode, scanCode, modifiers))
    }

    fun charTyped(codepoint: Char, modifiers: Int) {
        root.triggerEvent(GuiLayerEvents.CharTyped(codepoint, modifiers))
    }

    fun render() {
        try {
            root.pos = vec(0, 0)
            root.scale = Client.guiScaleFactor
            root.size = vec(Client.window.scaledWidth, Client.window.scaledHeight)
            main.pos = ((root.size - main.size) / 2).round()

            root.triggerEvent(GuiLayerEvents.Update())
//            updateLayout()
//            drawComponents()

            val context = GuiDrawContext(Matrix3dStack(), false)
            root.renderLayer(context)
        } catch (e: Exception) {
            logger.error("Error in GUI:", e)
            Client.displayGuiScreen(SafetyNetErrorScreen(e))
        }
    }
}

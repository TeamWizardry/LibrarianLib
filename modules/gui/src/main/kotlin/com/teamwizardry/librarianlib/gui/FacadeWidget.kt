package com.teamwizardry.librarianlib.gui

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.gui.component.GuiDrawContext
import com.teamwizardry.librarianlib.gui.provided.SafetyNetErrorScreen
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.gui.IGuiEventListener
import net.minecraft.client.gui.INestedGuiEventHandler
import net.minecraft.client.gui.IRenderable
import net.minecraft.client.gui.screen.Screen
import org.lwjgl.glfw.GLFW
import java.util.ArrayDeque

/**
 *
 */
open class FacadeWidget(
    private val screen: Screen
) {
    val root = GuiComponent()

    /**
     * We keep track of the mouse position ourselves both so we can provide deltas for move events and so we can provide
     * subpixel mouse positions in [render]
     */
    private var mouseX = 0.0
    private var mouseY = 0.0

    fun mouseMoved(xPos: Double, yPos: Double) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiComponentEvents.MouseMove(vec(xPos, yPos), vec(mouseX, mouseY)))
        mouseX = xPos
        mouseY = yPos
    }

    fun mouseClicked(xPos: Double, yPos: Double, button: Int) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiComponentEvents.MouseDown(vec(xPos, yPos), button))
    }

    fun isMouseOver(xPos: Double, yPos: Double) {
    }

    fun mouseReleased(xPos: Double, yPos: Double, button: Int) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiComponentEvents.MouseUp(vec(xPos, yPos), button))
    }

    fun mouseScrolled(xPos: Double, yPos: Double, delta: Double) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiComponentEvents.MouseScroll(vec(xPos, yPos), vec(0.0, delta)))
    }

    fun mouseDragged(xPos: Double, yPos: Double, button: Int, deltaX: Double, deltaY: Double) {
        computeMouseOver(xPos, yPos)
        root.triggerEvent(GuiComponentEvents.MouseDrag(vec(xPos, yPos), vec(xPos - deltaX, yPos - deltaY), button))
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
        root.triggerEvent(GuiComponentEvents.KeyDown(keyCode, scanCode, modifiers))
    }

    fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        root.triggerEvent(GuiComponentEvents.KeyUp(keyCode, scanCode, modifiers))
    }

    fun charTyped(codepoint: Char, modifiers: Int) {
        root.triggerEvent(GuiComponentEvents.CharTyped(codepoint, modifiers))
    }

    fun render() {
        try {
            root.pos = vec(0, 0)
            root.scale = Client.guiScaleFactor
            root.size = vec(Client.window.scaledWidth, Client.window.scaledHeight)

//            updateComponents()
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

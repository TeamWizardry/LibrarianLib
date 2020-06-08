package com.teamwizardry.librarianlib.facade

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.provided.SafetyNetErrorScreen
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.GameSettings
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
    private val tooltipContainer = GuiLayer()
    private var currentTooltip: GuiLayer? = null

    init {
        root.add(main, tooltipContainer)
        tooltipContainer.interactive = false
    }

    /**
     * We keep track of the mouse position ourselves both so we can provide deltas for move events and so we can provide
     * subpixel mouse positions in [render]
     */
    private var mouseX = 0.0
    private var mouseY = 0.0
    private var mouseOver: GuiLayer? = null

    fun mouseMoved(_xPos: Double, _yPos: Double) {
        val s = Client.guiScaleFactor // rescale to absolute screen coordinates
        val xPos = _xPos * s
        val yPos = _yPos * s
        computeMouseOver(xPos, yPos)
        safetyNet {
            root.triggerEvent(GuiLayerEvents.MouseMove(vec(xPos, yPos), vec(mouseX, mouseY)))
        }
        mouseX = xPos
        mouseY = yPos
    }

    fun mouseClicked(_xPos: Double, _yPos: Double, button: Int) {
        val s = Client.guiScaleFactor // rescale to absolute screen coordinates
        val xPos = _xPos * s
        val yPos = _yPos * s
        computeMouseOver(xPos, yPos)
        safetyNet {
            root.triggerEvent(GuiLayerEvents.MouseDown(vec(xPos, yPos), button))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun isMouseOver(xPos: Double, yPos: Double) {
    }

    fun mouseReleased(_xPos: Double, _yPos: Double, button: Int) {
        val s = Client.guiScaleFactor // rescale to absolute screen coordinates
        val xPos = _xPos * s
        val yPos = _yPos * s
        computeMouseOver(xPos, yPos)
        safetyNet {
            root.triggerEvent(GuiLayerEvents.MouseUp(vec(xPos, yPos), button))
        }
    }

    fun mouseScrolled(_xPos: Double, _yPos: Double, _delta: Double) {
        val s = Client.guiScaleFactor // rescale to absolute screen coordinates
        val xPos = _xPos * s
        val yPos = _yPos * s
        val delta = _delta * s
        computeMouseOver(xPos, yPos)
        safetyNet {
            root.triggerEvent(GuiLayerEvents.MouseScroll(vec(xPos, yPos), vec(0.0, delta)))
        }
    }

    fun mouseDragged(_xPos: Double, _yPos: Double, button: Int, _deltaX: Double, _deltaY: Double) {
        val s = Client.guiScaleFactor // rescale to absolute screen coordinates
        val xPos = _xPos * s
        val yPos = _yPos * s
        val deltaX = _deltaX * s
        val deltaY = _deltaY * s
        computeMouseOver(xPos, yPos)
        safetyNet {
            root.triggerEvent(GuiLayerEvents.MouseDrag(vec(xPos, yPos), vec(xPos - deltaX, yPos - deltaY), button))
        }
    }

    private fun computeMouseOver(xPos: Double, yPos: Double) {
        safetyNet {
            mouseOver = root.computeMouseInfo(vec(xPos, yPos), Matrix3dStack())
            generateSequence(mouseOver) { it.parent }.forEach {
                it.mouseOver = true
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun changeFocus(reverse: Boolean) {
        // todo
    }

    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            screen.onClose()
        }
        safetyNet {
            root.triggerEvent(GuiLayerEvents.KeyDown(keyCode, scanCode, modifiers))
        }
    }

    fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        safetyNet {
            root.triggerEvent(GuiLayerEvents.KeyUp(keyCode, scanCode, modifiers))
        }
    }

    fun charTyped(codepoint: Char, modifiers: Int) {
        safetyNet {
            root.triggerEvent(GuiLayerEvents.CharTyped(codepoint, modifiers))
        }
    }

    fun render() {
        safetyNet {
            val s = Client.guiScaleFactor // rescale to absolute screen coordinates
            root.pos = vec(0, 0)
            root.scale = s
            root.size = vec(Client.window.scaledWidth, Client.window.scaledHeight)
            main.pos = ((root.size - main.size) / 2).round()
            tooltipContainer.frame = root.bounds

            computeMouseOver(mouseX, mouseY)
            var tooltip: GuiLayer? = null
            var cursor: Cursor? = null
            generateSequence(mouseOver) { it.parent }.forEach {
                tooltip = tooltip ?: it.tooltipLayer
                cursor = cursor ?: it.cursor
            }

            if (tooltip != currentTooltip) {
                currentTooltip?.removeFromParent()
                currentTooltip = tooltip
                tooltip?.also { tooltipContainer.add(it) }
            }
            Cursor.setCursor(cursor)

            root.updateAnimations(Client.time.time)
            root.triggerEvent(GuiLayerEvents.Update())
            root.triggerEvent(GuiLayerEvents.PrepareLayout())
            root.runLayout()
            root.clearAllDirtyLayout()

            RenderSystem.pushMatrix()
            RenderSystem.scaled(1 / s, 1 / s, 1.0)
            val context = GuiDrawContext(Matrix3dStack(), Client.minecraft.renderManager.isDebugBoundingBox)
            root.renderLayer(context)
            RenderSystem.popMatrix()
        }
    }

    fun removed() {
        Cursor.setCursor(null)
    }

    private inline fun safetyNet(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            logger.error("Error in GUI:", e)
            Client.displayGuiScreen(SafetyNetErrorScreen(e))
        }
    }
}

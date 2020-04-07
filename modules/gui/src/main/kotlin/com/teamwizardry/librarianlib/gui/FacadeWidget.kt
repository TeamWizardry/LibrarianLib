package com.teamwizardry.librarianlib.gui

import com.teamwizardry.librarianlib.gui.components.StandaloneRootComponent
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.gui.IGuiEventListener
import net.minecraft.client.gui.IRenderable

open class FacadeWidget(closeGui: (Exception) -> Unit): IRenderable, IGuiEventListener {
    val root = StandaloneRootComponent(closeGui)

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
        root.renderRoot(partialTicks, vec(mouseX, mouseY))
    }
}

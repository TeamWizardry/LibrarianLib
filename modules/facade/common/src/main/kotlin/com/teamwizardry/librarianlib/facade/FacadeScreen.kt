package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.navigation.GuiNavigationPath
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

/**
 * The base class for all LibrarianLib GUIs.
 *
 * The [root] layer represents the entire screen, while the [main] layer is where the main content of your GUI should
 * be added.
 *
 * [main] is automatically repositioned to remain centered on the screen, so setting its size is the equivalent of
 * setting [xSize][ContainerScreen.xSize] and [ySize][ContainerScreen.ySize].
 * If [main] is too tall or too wide to fit on the screen at the current GUI scale it will attempt to downscale to fit
 * (decreasing the effective GUI scale setting until either the GUI fits or the scale reaches "Small"). [root] doesn't
 * scale with [main] and so always reflects Minecraft's GUI scale.
 *
 * Any crashes from the GUI code will be caught and displayed as an error screen instead of crashing the game. However,
 * it is impossible to wrap subclass constructors in try-catch statements so those may still crash.
 */
public open class FacadeScreen(title: Text): Screen(title) {
    @Suppress("LeakingThis")
    public val facade: FacadeWidget = FacadeWidget(this)

    /**
     * The most commonly-used root layer, positioned at the center of the screen, accounting for the layer size
     */
    public val main: GuiLayer = facade.main

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        this.facade.update()
        this.facade.render(context)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        facade.mouseMoved(mouseX, mouseY)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        return facade.charTyped(chr, modifiers)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return facade.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return facade.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        facade.mouseClicked(mouseX, mouseY, button)
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        facade.mouseReleased(mouseX, mouseY, button)
        return true
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        facade.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
        return true
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        facade.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        return true
    }

    override fun close() {
        super.close()
        facade.onClose()
    }
}

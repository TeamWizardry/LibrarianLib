package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.util.text.ITextComponent

/**
 * The base class for all LibrarianLib GUIs.
 *
 * The [root] layer represents the entire screen, while the [main] layer is where the main content of your GUI should
 * be added.
 *
 * [main] is automatically repositioned to remain centered on the screen, so setting its size is the equivalent of
 * setting its size is equivalent to setting [xSize][ContainerScreen.xSize] and [ySize][ContainerScreen.ySize].
 * If [main] is too tall or too wide to fit on the screen at the current GUI scale it will attempt to downscale to fit
 * (decreasing the effective GUI scale setting until either the GUI fits or the scale reaches "Small"). [root] doesn't
 * scale with [main] and so always reflects Minecraft's GUI scale.
 *
 * Any crashes from the GUI code will be caught and displayed as an error screen instead of crashing the game. However,
 * it is impossible to wrap subclass constructors in try-catch statements so those may still crash.
 */
public open class FacadeScreen(title: ITextComponent): Screen(title /* todo behavior #2 */) {
    @Suppress("LeakingThis")
    public val facade: FacadeWidget = FacadeWidget(this)

    /**
     * The most commonly-used root layer, positioned at the center of the screen, accounting for the layer size
     */
    public val main: GuiLayer = facade.main

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground()
        super.render(mouseX, mouseY, partialTicks)
        this.facade.update()
        this.facade.render()
    }

    override fun mouseMoved(xPos: Double, p_212927_3_: Double) {
        facade.mouseMoved(xPos, p_212927_3_)
    }

    override fun charTyped(p_charTyped_1_: Char, p_charTyped_2_: Int): Boolean {
        return facade.charTyped(p_charTyped_1_, p_charTyped_2_)
    }

//    override fun func_212932_b(eventListener: IGuiEventListener?) {
//        facade.func_212932_b(eventListener)
//    }

    //    override fun getEventListenerForPos(mouseX: Double, mouseY: Double): Optional<IGuiEventListener> {
//        return facade.getEventListenerForPos(mouseX, mouseY)
//    }
    override fun keyPressed(p_keyPressed_1_: Int, p_keyPressed_2_: Int, p_keyPressed_3_: Int): Boolean {
        return facade.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return facade.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun mouseClicked(p_mouseClicked_1_: Double, p_mouseClicked_3_: Double, p_mouseClicked_5_: Int): Boolean {
        facade.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)
        return true
    }

    override fun mouseReleased(p_mouseReleased_1_: Double, p_mouseReleased_3_: Double, p_mouseReleased_5_: Int): Boolean {
        facade.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)
        return true
    }

    override fun mouseScrolled(p_mouseScrolled_1_: Double, p_mouseScrolled_3_: Double, p_mouseScrolled_5_: Double): Boolean {
        facade.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_)
        return true
    }

//    override fun setFocusedDefault(eventListener: IGuiEventListener?) {
//        facade.setFocusedDefault(eventListener)
//    }

    override fun mouseDragged(p_mouseDragged_1_: Double, p_mouseDragged_3_: Double, p_mouseDragged_5_: Int, p_mouseDragged_6_: Double, p_mouseDragged_8_: Double): Boolean {
        facade.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_)
        return true
    }

    override fun changeFocus(p_changeFocus_1_: Boolean): Boolean {
        facade.changeFocus(p_changeFocus_1_)
        return true
    }

    override fun removed() {
        super.removed()
        facade.removed()
    }
}

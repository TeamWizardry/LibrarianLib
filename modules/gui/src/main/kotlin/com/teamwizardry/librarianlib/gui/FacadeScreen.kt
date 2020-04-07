package com.teamwizardry.librarianlib.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.IGuiEventListener
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.text.ITextComponent
import java.util.Optional

/**
 * The base class for all LibrarianLib GUIs.
 *
 * The [root] component represents the entire screen, while the [main] component is where the main content of your GUI
 * should be added.
 *
 * [main] is automatically repositioned to remain centered on the screen, so setting its size is the equivalent of
 * setting its size is equivalent to setting [xSize][GuiContainer.xSize] and [ySize][GuiContainer.ySize].
 * If [main] is too tall or too wide to fit on the screen at the current GUI scale it will attempt to downscale to fit
 * (decreasing the effective GUI scale setting until either the GUI fits or the scale reaches "Small"). [root] doesn't
 * scale with [main] and so always reflects Minecraft's GUI scale.
 *
 * Any crashes from the GUI code will be caught and displayed as an error screen instead of crashing the game. However,
 * it is impossible to wrap subclass constructors in try-catch statements so those may still crash.
 */
open class FacadeScreen(title: ITextComponent): Screen(title /* todo behavior #2 */) {
    @Suppress("LeakingThis")
    val facade = FacadeWidget(this)

    init {
        children.add(facade)
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground()
        super.render(mouseX, mouseY, partialTicks)
        RenderSystem.disableBlend()
        this.facade.render(mouseX, mouseY, partialTicks)
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

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return facade.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun mouseClicked(p_mouseClicked_1_: Double, p_mouseClicked_3_: Double, p_mouseClicked_5_: Int): Boolean {
        return facade.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)
    }

    override fun mouseReleased(p_mouseReleased_1_: Double, p_mouseReleased_3_: Double, p_mouseReleased_5_: Int): Boolean {
        return facade.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)
    }

    override fun mouseScrolled(p_mouseScrolled_1_: Double, p_mouseScrolled_3_: Double, p_mouseScrolled_5_: Double): Boolean {
        return facade.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_)
    }

//    override fun setFocusedDefault(eventListener: IGuiEventListener?) {
//        facade.setFocusedDefault(eventListener)
//    }

    override fun mouseDragged(p_mouseDragged_1_: Double, p_mouseDragged_3_: Double, p_mouseDragged_5_: Int, p_mouseDragged_6_: Double, p_mouseDragged_8_: Double): Boolean {
        return facade.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_)
    }

    override fun changeFocus(p_changeFocus_1_: Boolean): Boolean {
        return facade.changeFocus(p_changeFocus_1_)
    }
}

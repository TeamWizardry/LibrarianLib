package com.teamwizardry.librarianlib.gui

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.getValue
import com.teamwizardry.librarianlib.core.util.kotlin.setValue
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.gui.provided.SafetyNetErrorScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.Style
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.io.IOException
import java.util.stream.Stream

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
open class GuiBase(title: ITextComponent): Screen(title /* todo behavior #2 */) {
    /**
     * The GUI implementation code common between [GuiBase] and [GuiContainerBase]
     */
    val impl = LibGuiImpl(
        { this.width },
        { this.height },
        {
            logger.error("The safety net caught an error", it)
            Client.minecraft.displayGuiScreen(SafetyNetErrorScreen(it))
        }
    )

    /**
     * The main component of the GUI, within which the contents of most GUIs will be placed. It will always center
     * itself in the screen and will dynamically adjust its effective GUI scale if it can't fit on the screen.
     */
    val main: GuiComponent by impl::main
    /**
     * The root component, whose position and size represents the entirety of the game screen.
     */
    val root: GuiComponent by impl::root
    /**
     * Whether to enable the Safety Net feature. By default this is true in development environments and false
     * elsewhere, but can be enabled/disabled manually.
     */
    val safetyNet: Boolean by impl::safetyNet
    /**
     * Whether to enable Minecraft's standard translucent GUI background.
     */
    var useDefaultBackground by impl::useDefaultBackground

    /**
     * Whether to close the GUI when the escape key is pressed
     */
    var escapeClosesGUI by impl::escapeClosesGUI

    /**
     * Automatic hook into the [LayoutChildren][GuiLayerEvents.LayoutChildren] event on [main]
     */
    open fun layoutMain() {
    }

    /**
     * Automatic hook into the [LayoutChildren][GuiLayerEvents.LayoutChildren] event on [root]
     */
    open fun layoutRoot() {
    }

    /**
     * Stores the current GUI to reopen it when this GUI is closed
     */
    fun reopenLast() {
        impl.lastGui = Client.minecraft.currentScreen
    }

    init {
        main.BUS.hook<GuiLayerEvents.LayoutChildren> {
            layoutMain()
        }
        root.BUS.hook<GuiLayerEvents.LayoutChildren> {
            layoutRoot()
        }
    }

    override fun init() {
        impl.initGui()
    }

    override fun onClose() {
        impl.onClose()
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(mouseX, mouseY, partialTicks)
        impl.drawScreen(mouseX, mouseY, partialTicks)
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super.mouseClicked(mouseX, mouseY, button)
        impl.mouseClicked(button)
        return true // todo behavior #1
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super.mouseReleased(mouseX, mouseY, button)
        impl.mouseReleased(button)
        return true // todo behavior #1
    }

    override fun charTyped(typedChar: Char, keyCode: Int): Boolean {
        impl.keyTyped(typedChar, keyCode)
        return true // todo behavior #1
    }

    @Throws(IOException::class)
    override fun handleKeyboardInput() {
        super.handleKeyboardInput()
        impl.handleKeyboardInput()
    }

    @Throws(IOException::class)
    override fun handleMouseInput() {
        super.handleMouseInput()
        impl.handleMouseInput()
    }

    override fun tick() {
        impl.update() // todo update #4
        impl.tick()
    }
}

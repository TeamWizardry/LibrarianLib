package com.teamwizardry.librarianlib.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.text.ITextComponent

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
    val facade = FacadeWidget {
        this.onClose()
    }

    init {
        children.add(facade)
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground()
        super.render(mouseX, mouseY, partialTicks)
        RenderSystem.disableBlend()
        this.facade.render(mouseX, mouseY, partialTicks)
    }
}

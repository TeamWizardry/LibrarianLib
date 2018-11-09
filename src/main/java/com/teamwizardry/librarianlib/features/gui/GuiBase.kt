package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.io.IOException

open class GuiBase : GuiScreen() {
    val impl = LibGuiImpl(
        { this.width }, { this.height }, { this.adjustGuiSize() }
    )

    val main: GuiComponent by impl::main.delegate
    val root: GuiComponent by impl::root.delegate
    var useDefaultBackground by impl::useDefaultBackground.delegate

    override fun initGui() {
        impl.initGui()
    }

    /**
     * Try to fit the gui in a [width] by [height] area, setting [guiWidth] and [guiHeight] to whatever size you manage
     * to clamp the GUI to.
     *
     * Return true from this function to cancel any auto resizing
     */
    open fun adjustGuiSize(): Boolean {
        return false
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        impl.drawScreen(mouseX, mouseY, partialTicks)
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        super.mouseClicked(mouseX, mouseY, button)
        impl.mouseClicked(button)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        super.mouseReleased(mouseX, mouseY, button)
        impl.mouseReleased(button)
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

    fun tick() {
        impl.tick()
    }

    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        @Suppress("UNUSED_PARAMETER")
        fun tick(e: TickEvent.ClientTickEvent) {
            val gui = Minecraft.getMinecraft().currentScreen
            if (gui is GuiBase) {
                gui.tick()
            }
        }
    }
}

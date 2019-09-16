package com.teamwizardry.librarianlib.features.facadecontainer

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.features.facade.LibGuiImpl
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.provided.GuiSafetyNetError
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.kotlin.getValue
import com.teamwizardry.librarianlib.features.kotlin.setValue
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.network.PacketSyncSlotVisibility
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.IOException

/**
 * Created by TheCodeWarrior
 */
@Suppress("LeakingThis")
open class GuiContainerBase(val container: ContainerBase) : GuiContainer(ContainerImpl(container)) {

    val impl = LibGuiImpl(
        { this.width },
        { this.height },
        {
            LibrarianLog.error(it, "The safety net caught an error")
            Client.minecraft.displayGuiScreen(GuiSafetyNetError(it))
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

    init {
        main.BUS.hook<GuiLayerEvents.LayoutChildren> {
            layoutMain()
        }
        root.BUS.hook<GuiLayerEvents.LayoutChildren> {
            layoutRoot()
        }
    }

    init {
        useDefaultBackground = true
    }

    override fun initGui() {
        super.initGui()
        impl.initGui()
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        impl.onClose()
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        // nop
    }
    override fun drawDefaultBackground() {
        // nop
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        container.allSlots.forEach { it.lastVisible = it.visible; it.visible = false }

        ContainerSpace.origin = main.convertPointTo(vec(0, 0), ScreenSpace)//vec(this.getGuiLeft(), this.getGuiTop())
        impl.drawScreen(mouseX, mouseY, partialTicks)

        if (container.allSlots.any { it.lastVisible != it.visible }) {
            PacketHandler.NETWORK.sendToServer(PacketSyncSlotVisibility(container.allSlots.map { it.visible }.toBooleanArray()))
        }
        container.allSlots.filter { !it.visible }.forEach { it.xPos = -1000; it.yPos = -1000 }
        GlStateManager.enableTexture2D()

        super.drawScreen(mouseX, mouseY, partialTicks)
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

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        impl.keyTyped(typedChar, keyCode)
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

    override fun updateScreen() {
        impl.update()
        this.xSize = main.widthi
        this.ySize = main.heighti
        this.guiLeft = (this.width - this.xSize) / 2
        this.guiTop = (this.height - this.ySize) / 2
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
            if (gui is GuiContainerBase) {
                gui.tick()
            }
        }
    }
}

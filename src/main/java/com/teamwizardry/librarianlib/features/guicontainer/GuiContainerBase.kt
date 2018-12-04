package com.teamwizardry.librarianlib.features.guicontainer

import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.LibGuiImpl
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.network.PacketSyncSlotVisibility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.io.IOException
import kotlin.coroutines.CoroutineContext

/**
 * Created by TheCodeWarrior
 */
@Suppress("LeakingThis")
open class GuiContainerBase(val container: ContainerBase, var guiWidth: Int, var guiHeight: Int) : GuiContainer(ContainerImpl(container)), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Client

    val impl = LibGuiImpl(
        { this.width }, { this.height }
    )

    val main: GuiComponent by impl::main.delegate
    val root: GuiComponent by impl::root.delegate
    var useDefaultBackground by impl::useDefaultBackground.delegate

    init {
        useDefaultBackground = true
    }

    override fun initGui() {
        impl.initGui()
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        // nop
    }
    override fun drawDefaultBackground() {
        // nop
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        container.allSlots.forEach { it.lastVisible = it.visible; it.visible = false }

        impl.drawScreen(mouseX, mouseY, partialTicks)

        if (container.allSlots.any { it.lastVisible != it.visible }) {
            PacketHandler.NETWORK.sendToServer(PacketSyncSlotVisibility(container.allSlots.map { it.visible }.toBooleanArray()))
        }
        container.allSlots.filter { !it.visible }.forEach { it.xPos = -1000; it.yPos = -1000 }

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
            if (gui is GuiContainerBase) {
                gui.tick()
            }
        }
    }
}

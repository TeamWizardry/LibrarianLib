package com.teamwizardry.librarianlib.features.neoguicontainer

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.features.facade.LibGuiImpl
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.provided.GuiSafetyNetError
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
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
import java.io.IOException
import kotlin.coroutines.CoroutineContext

/**
 * Created by TheCodeWarrior
 */
@Suppress("LeakingThis")
open class GuiContainerBase(val container: ContainerBase) : GuiContainer(ContainerImpl(container)), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Client

    val impl = LibGuiImpl(
        { this.width },
        { this.height },
        {
            LibrarianLog.error(it, "The safety net caught an error")
            Minecraft().displayGuiScreen(GuiSafetyNetError(it))
        }
    )

    val main: GuiComponent by impl::main.delegate
    val root: GuiComponent by impl::root.delegate
    val safetyNet: Boolean by impl::safetyNet.delegate
    var useDefaultBackground by impl::useDefaultBackground.delegate

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

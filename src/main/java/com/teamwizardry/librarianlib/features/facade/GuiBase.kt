package com.teamwizardry.librarianlib.features.facade

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.provided.GuiSafetyNetError
import com.teamwizardry.librarianlib.features.neoguicontainer.GuiContainerBase
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.delegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.IOException
import kotlin.coroutines.CoroutineContext

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
 * In development environments any crashes from the GUI code will be caught and displayed as an error screen instead of
 * crashing the game. However, it is impossible to wrap subclass constructors in try-catch statements so those will
 * still crash.
 */
open class GuiBase : GuiScreen() {
    /**
     * The GUI implementation code common between [GuiBase] and [GuiContainerBase]
     */
    val impl = LibGuiImpl(
        { this.width },
        { this.height },
        {
            LibrarianLog.error(it, "The safety net caught an error")
            Minecraft().displayGuiScreen(GuiSafetyNetError(it))
        }
    )

    /**
     * The main component of the GUI, within which the contents of most GUIs will be placed. It will always center
     * itself in the screen and will dynamically adjust its effective GUI scale if it can't fit on the screen.
     */
    val main: GuiComponent by impl::main.delegate
    /**
     * The root component, whose position and size represents the entirety of the game screen.
     */
    val root: GuiComponent by impl::root.delegate
    /**
     * Whether to enable the Safety Net feature. By default this is true in development environments and false
     * elsewhere, but can be enabled/disabled manually.
     */
    val safetyNet: Boolean by impl::safetyNet.delegate
    /**
     * Whether to enable Minecraft's standard translucent GUI background.
     */
    var useDefaultBackground by impl::useDefaultBackground.delegate

    override fun initGui() {
        impl.initGui()
    }

    override fun onGuiClosed() {
        impl.onClose()
    }

    override fun isFocused(): Boolean {
        return impl.root.focusedComponent != null
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

    override fun updateScreen() {
        impl.update()
    }

    internal fun tick() {
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

package com.teamwizardry.librarianlib.features.guicontainer

import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.network.PacketSyncSlotVisibility
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.config.GuiUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.io.IOException

/**
 * Created by TheCodeWarrior
 */
open class GuiContainerBase(val container: ContainerBase, var guiWidth: Int, var guiHeight: Int) : GuiContainer(ContainerImpl(container)) {
    protected val mainComponents: ComponentVoid = ComponentVoid(0, 0)
    protected val fullscreenComponents: ComponentVoid = ComponentVoid(0, 0)
    private val mainScaleWrapper: ComponentVoid = ComponentVoid(0, 0)

    init {
        fullscreenComponents.setData(GuiContainerBase::class.java, this)
        mainComponents.calculateOwnHover = false
        fullscreenComponents.calculateOwnHover = false
        mainScaleWrapper.zIndex = -100000 // really far back
        fullscreenComponents.add(mainScaleWrapper)
        mainScaleWrapper.add(mainComponents)

        mainComponents.size = vec(guiWidth, guiHeight)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    }

    override fun initGui() {
        super.initGui()
        guiLeft = 0
        guiTop = 0
        var s = 1.0
        if (!adjustGuiSize()) {
            var i = 1
            // find required scale, either 1x, 1/2x 1/3x, or 1/4x
            while ((guiWidth * s > width || guiHeight * s > height) && i < 4) {
                i++
                s = 1.0 / i
            }
        }

        val left = (width / 2 - guiWidth * s / 2).toInt()
        val top = (height / 2 - guiHeight * s / 2).toInt()

        if (mainScaleWrapper.pos.xi != left || mainScaleWrapper.pos.yi != top) {
            mainScaleWrapper.pos = vec(left, top)
            mainScaleWrapper.childScale = s
            mainScaleWrapper.size = vec(guiWidth * s, guiHeight * s)
        }

        fullscreenComponents.size = vec(width, height)
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


    override fun drawDefaultBackground() { /* NOOP */
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        container.allSlots.forEach { it.lastVisible = it.visible; it.visible = false }
        super.drawDefaultBackground()
        GlStateManager.pushAttrib()
        GlStateManager.enableBlend()
        val relPos = vec(mouseX, mouseY)
        fullscreenComponents.calculateMouseOver(relPos)
        fullscreenComponents.draw(relPos, partialTicks)

        if (fullscreenComponents.tooltipText != null) {
            GuiUtils.drawHoveringText(fullscreenComponents.tooltipText, mouseX, mouseY, width, height, -1, if (fullscreenComponents.tooltipFont == null) mc.fontRendererObj else fullscreenComponents.tooltipFont)
            fullscreenComponents.tooltipText = null
            fullscreenComponents.tooltipFont = null
        }
        GlStateManager.disableBlend()
        GlStateManager.enableTexture2D()
        GlStateManager.popAttrib()

        if (container.allSlots.any { it.lastVisible != it.visible }) {
            PacketHandler.NETWORK.sendToServer(PacketSyncSlotVisibility(container.allSlots.map { it.visible }.toBooleanArray()))
        }

        container.allSlots.filter { !it.visible }.forEach { it.xPos = -1000; it.yPos = -1000 }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        fullscreenComponents.mouseDown(vec(mouseX, mouseY), EnumMouseButton.getFromCode(mouseButton))
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        fullscreenComponents.mouseUp(vec(mouseX, mouseY), EnumMouseButton.getFromCode(state))
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        fullscreenComponents.mouseDrag(vec(mouseX, mouseY), EnumMouseButton.getFromCode(clickedMouseButton))
    }

    @Throws(IOException::class)
    override fun handleKeyboardInput() {
        super.handleKeyboardInput()

        if (Keyboard.getEventKeyState())
            fullscreenComponents.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        else
            fullscreenComponents.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey())
    }

    @Throws(IOException::class)
    override fun handleMouseInput() {
        super.handleMouseInput()
        val mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth
        val mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1
        val wheelAmount = Mouse.getEventDWheel()

        if (wheelAmount != 0) {
            fullscreenComponents.mouseWheel(vec(mouseX, mouseY), GuiComponent.MouseWheelDirection.fromSign(wheelAmount))
        }
    }

    fun tick() {
        fullscreenComponents.tick()
    }

    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun tick(e: TickEvent.ClientTickEvent) {
            val gui = Minecraft.getMinecraft().currentScreen
            if (gui is GuiContainerBase) {
                gui.tick()
            }
        }
    }
}

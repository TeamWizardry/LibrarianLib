package com.teamwizardry.librarianlib.client.gui

import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.config.GuiUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.io.IOException

open class GuiBase(protected var guiWidth: Int, protected var guiHeight: Int) : GuiScreen() {
    protected val mainComponents: ComponentVoid = ComponentVoid(0, 0)
    protected val fullscreenComponents: ComponentVoid = ComponentVoid(0, 0)
    protected var top: Int = 0
    protected var left: Int = 0

    init {
        mainComponents.calculateOwnHover = false
        fullscreenComponents.calculateOwnHover = false
        mainComponents.zIndex = -100000 // really far back
        fullscreenComponents.add(mainComponents)
    }

    override fun initGui() {
        super.initGui()
        left = width / 2 - guiWidth / 2
        top = height / 2 - guiHeight / 2

        if (mainComponents.pos.xi != left || mainComponents.pos.yi != top)
            mainComponents.pos = vec(left, top)
        fullscreenComponents.size = vec(width, height)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        val relPos = vec(mouseX, mouseY)
        fullscreenComponents.calculateMouseOver(relPos)
        fullscreenComponents.draw(relPos, partialTicks)

        if (fullscreenComponents.tooltipText != null) {
            GuiUtils.drawHoveringText(fullscreenComponents.tooltipText, mouseX, mouseY, width, height, -1, if (fullscreenComponents.tooltipFont == null) mc.fontRendererObj else fullscreenComponents.tooltipFont)
            fullscreenComponents.tooltipText = null
            fullscreenComponents.tooltipFont = null
        }
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
        @Suppress("UNUSED_PARAMETER")
        fun tick(e: TickEvent.ClientTickEvent) {
            val gui = Minecraft.getMinecraft().currentScreen
            if(gui is GuiBase) {
                gui.tick()
            }
        }
    }
}

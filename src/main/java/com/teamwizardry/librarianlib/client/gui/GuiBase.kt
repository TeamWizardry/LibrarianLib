package com.teamwizardry.librarianlib.client.gui

import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.config.GuiUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.io.IOException

open class GuiBase(protected var guiWidth: Int, protected var guiHeight: Int) : GuiScreen() {
    protected val components: ComponentVoid = ComponentVoid(0, 0)
    protected val fullscreenComponents: ComponentVoid = ComponentVoid(0, 0)
    protected var top: Int = 0
    protected var left: Int = 0

    init {
        components.calculateOwnHover = false
        fullscreenComponents.calculateOwnHover = false
        components.zIndex = -100000 // really far back
        fullscreenComponents.add(components)
    }

    override fun initGui() {
        super.initGui()
        left = width / 2 - guiWidth / 2
        top = height / 2 - guiHeight / 2

        if (components.pos.xi != left || components.pos.yi != top)
            components.pos = Vec2d(left.toDouble(), top.toDouble())
        fullscreenComponents.size = Vec2d(width.toDouble(), height.toDouble())
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        val relPos = Vec2d(mouseX.toDouble(), mouseY.toDouble())
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
        fullscreenComponents.mouseDown(Vec2d(mouseX.toDouble(), mouseY.toDouble()), EnumMouseButton.getFromCode(mouseButton))
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        fullscreenComponents.mouseUp(Vec2d(mouseX.toDouble(), mouseY.toDouble()), EnumMouseButton.getFromCode(state))
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        fullscreenComponents.mouseDrag(Vec2d(mouseX.toDouble(), mouseY.toDouble()), EnumMouseButton.getFromCode(clickedMouseButton))
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
            fullscreenComponents.mouseWheel(Vec2d(mouseX.toDouble(), mouseY.toDouble()), GuiComponent.MouseWheelDirection.fromSign(wheelAmount))
        }
    }
}

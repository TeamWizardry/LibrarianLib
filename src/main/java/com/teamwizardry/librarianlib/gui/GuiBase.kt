package com.teamwizardry.librarianlib.gui

import java.io.IOException

import net.minecraftforge.fml.client.config.GuiUtils

import net.minecraft.client.gui.GuiScreen

import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

import com.teamwizardry.librarianlib.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.math.Vec2d

open class GuiBase(protected var guiWidth: Int, protected var guiHeight: Int) : GuiScreen() {
    protected var components: ComponentVoid
    protected var top: Int = 0
    protected var left: Int = 0

    init {
        components = ComponentVoid(0, 0)
        components.setCalculateOwnHover(false)
    }

    override fun initGui() {
        super.initGui()
        left = width / 2 - guiWidth / 2
        top = height / 2 - guiHeight / 2

        if (components.pos.xi != left || components.pos.yi != top)
            components.pos = Vec2d(left.toDouble(), top.toDouble())
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        components.draw(components.relativePos(Vec2d(mouseX.toDouble(), mouseY.toDouble())), partialTicks)

        if (components.tooltipText != null) {
            GuiUtils.drawHoveringText(components.tooltipText, mouseX, mouseY, width, height, -1, if (components.tooltipFont == null) mc.fontRendererObj else components.tooltipFont)
            components.tooltipText = null
            components.tooltipFont = null
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        components.mouseDown(components.relativePos(Vec2d(mouseX.toDouble(), mouseY.toDouble())), EnumMouseButton.getFromCode(mouseButton))
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        components.mouseUp(components.relativePos(Vec2d(mouseX.toDouble(), mouseY.toDouble())), EnumMouseButton.getFromCode(state))
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        components.mouseDrag(components.relativePos(Vec2d(mouseX.toDouble(), mouseY.toDouble())), EnumMouseButton.getFromCode(clickedMouseButton))
    }

    @Throws(IOException::class)
    override fun handleKeyboardInput() {
        super.handleKeyboardInput()

        if (Keyboard.getEventKeyState())
            components.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        else
            components.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey())
    }

    @Throws(IOException::class)
    override fun handleMouseInput() {
        super.handleMouseInput()
        val mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth
        val mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1
        var wheelAmount = Mouse.getEventDWheel()

        if (wheelAmount != 0) {
            if (wheelAmount > 0) {
                wheelAmount = 1
            }

            if (wheelAmount < 0) {
                wheelAmount = -1
            }

            components.mouseWheel(components.relativePos(Vec2d(mouseX.toDouble(), mouseY.toDouble())), wheelAmount)
        }
    }
}

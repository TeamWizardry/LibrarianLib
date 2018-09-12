package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.debugger.ComponentDebugger
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.io.IOException

open class LibGuiImpl(
    var mainWidth: Int, var mainHeight: Int,
    protected val guiWidth: () -> Int, protected val guiHeight: () -> Int, protected val adjustGuiSize: () -> Boolean
    ) {
    val mainComponents: ComponentVoid = ComponentVoid(0, 0)
    val fullscreenComponents: ComponentVoid = ComponentVoid(0, 0)
    private val mainScaleWrapper: ComponentVoid = ComponentVoid(0, 0)
    private var isDebugMode = false
    private val debugger = ComponentDebugger()

    init {
        mainComponents.shouldCalculateOwnHover = false
        fullscreenComponents.shouldCalculateOwnHover = false
        mainScaleWrapper.zIndex = -100000 // really far back
        fullscreenComponents.add(mainScaleWrapper)
        mainScaleWrapper.add(mainComponents)

        mainComponents.size = vec(mainWidth, mainHeight)
        debugger.shouldCalculateOwnHover = false
    }

    fun initGui() {
        var s = 1.0
        if (!adjustGuiSize()) {
            var i = 1
            // find required scale, either 1x, 1/2x 1/3x, or 1/4x
            while ((mainWidth * s > guiWidth() || mainHeight * s > guiHeight()) && i < 4) {
                i++
                s = 1.0 / i
            }
        }

        val left = (guiWidth() / 2 - mainWidth * s / 2).toInt()
        val top = (guiHeight() / 2 - mainHeight * s / 2).toInt()

        if (mainScaleWrapper.pos.xi != left || mainScaleWrapper.pos.yi != top) {
            mainScaleWrapper.pos = vec(left, top)
            mainScaleWrapper.transform.scale = s
            mainScaleWrapper.size = vec(mainWidth * s, mainHeight * s)
        }

        fullscreenComponents.size = vec(guiWidth(), guiHeight())


        val scaledresolution = ScaledResolution(Minecraft.getMinecraft())

        debugger.transform.scale = 1.0 / scaledresolution.scaleFactor
        debugger.size = vec(guiWidth() * scaledresolution.scaleFactor, guiHeight() * scaledresolution.scaleFactor)
    }

    fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.enableBlend()
        val relPos = vec(mouseX, mouseY)
        GlStateManager.pushMatrix()

        if (isDebugMode) {
            GlStateManager.translate(guiWidth() / 2.0, guiHeight() / 2.0, 0.0)
            GlStateManager.rotate(-20f, 1f, 0f, 0f)
            GlStateManager.rotate(-20f, 0f, 1f, 0f)
            GlStateManager.translate(-guiWidth() / 2.0, -guiHeight() / 2.0, 0.0)
        }

        fullscreenComponents.renderRoot(relPos, partialTicks)

        GlStateManager.popMatrix()

        if (isDebugMode) {
            debugger.renderRoot(relPos, partialTicks)
        }

        fullscreenComponents.drawLate(partialTicks)
        Mouse.setNativeCursor((debugger.cursor ?: fullscreenComponents.cursor)?.lwjglCursor)
        debugger.cursor = null
        fullscreenComponents.cursor = null
    }

    @Throws(IOException::class)
    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (isDebugMode) debugger.mouseDown(vec(mouseX, mouseY), EnumMouseButton.getFromCode(mouseButton))
        if (!isDebugMode || !debugger.mouseOver)
            fullscreenComponents.mouseDown(vec(mouseX, mouseY), EnumMouseButton.getFromCode(mouseButton))
    }

    fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        if (isDebugMode) debugger.mouseUp(vec(mouseX, mouseY), EnumMouseButton.getFromCode(state))
        if (!isDebugMode || !debugger.mouseOver)
            fullscreenComponents.mouseUp(vec(mouseX, mouseY), EnumMouseButton.getFromCode(state))
    }

    fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        if (isDebugMode) debugger.mouseDrag(vec(mouseX, mouseY), EnumMouseButton.getFromCode(clickedMouseButton))
        if (!isDebugMode || !debugger.mouseOver)
            fullscreenComponents.mouseDrag(vec(mouseX, mouseY), EnumMouseButton.getFromCode(clickedMouseButton))
    }

    @Throws(IOException::class)
    fun handleKeyboardInput() {
        if (Keyboard.getEventKeyState()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_D &&
                    (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) &&
                    (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
                    ) {
                isDebugMode = !isDebugMode
            }

            if (Keyboard.getEventKey() == Keyboard.KEY_B && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                Minecraft.getMinecraft().renderManager.isDebugBoundingBox = !Minecraft.getMinecraft().renderManager.isDebugBoundingBox
            }
        }

        if (Keyboard.getEventKeyState()) {
            if (isDebugMode) debugger.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey())
            if (!isDebugMode || !debugger.mouseOver)
                fullscreenComponents.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        } else {
            if (isDebugMode) debugger.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey())
            if (!isDebugMode || !debugger.mouseOver)
                fullscreenComponents.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        }
    }

    @Throws(IOException::class)
    fun handleMouseInput() {
        val mouseX = Mouse.getEventX() * this.guiWidth() / Minecraft().displayWidth
        val mouseY = this.guiHeight() - Mouse.getEventY() * this.guiHeight() / Minecraft().displayHeight - 1
        val wheelAmount = Mouse.getEventDWheel()

        if (wheelAmount != 0) {
            if (isDebugMode) debugger.mouseWheel(vec(mouseX, mouseY), GuiComponentEvents.MouseWheelDirection.fromSign(wheelAmount))
            if (!isDebugMode || !debugger.mouseOver)
                fullscreenComponents.mouseWheel(vec(mouseX, mouseY), GuiComponentEvents.MouseWheelDirection.fromSign(wheelAmount))
        }
    }

    fun tick() {
        if (isDebugMode) debugger.tick()
        fullscreenComponents.tick()
    }
}

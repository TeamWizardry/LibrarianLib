package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.components.RootComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.io.IOException

open class LibGuiImpl(
    protected val guiWidth: () -> Int, protected val guiHeight: () -> Int, protected val adjustGuiSize: () -> Boolean
    ) {
    val root: GuiComponent = RootComponent()
    val main: GuiComponent = object: GuiComponent(0, 0) {
        override var pos: Vec2d
            get() = root.size/2
            set(value) {}
        override var anchor: Vec2d
            get() = vec(0.5, 0.5)
            set(value) {}
    }

    init {
        main.shouldComputeMouseInsideFromBounds = false
        root.shouldComputeMouseInsideFromBounds = false
        root.add(main)
    }

    fun initGui() {

        var s = 1.0
        if (!adjustGuiSize()) {
            var i = 1
            // find required scale, either 1x, 1/2x 1/3x, or 1/4x
            while ((main.size.x * s > guiWidth() || main.size.y * s > guiHeight()) && i < 4) {
                i++
                s = 1.0 / i
            }
        }

        val guiSize = vec(guiWidth(), guiHeight())

        main.pos = guiSize/2
        main.scale = s

        root.size = guiSize
    }

    fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.enableBlend()
        val relPos = vec(mouseX, mouseY)
        GlStateManager.pushMatrix()

        root.updateMouse(relPos)
        root.updateMouseInside()
        root.updateMouseOver(false)
        root.renderRoot(partialTicks)

        GlStateManager.popMatrix()

        Mouse.setNativeCursor(root.cursor?.lwjglCursor)
        root.cursor = null
    }

    @Throws(IOException::class)
    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
            root.mouseDown(EnumMouseButton.getFromCode(mouseButton))
    }

    fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
            root.mouseUp(EnumMouseButton.getFromCode(state))
    }

    fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
            root.mouseDrag(EnumMouseButton.getFromCode(clickedMouseButton))
    }

    @Throws(IOException::class)
    fun handleKeyboardInput() {
        if (Keyboard.getEventKeyState()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_B && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                Minecraft.getMinecraft().renderManager.isDebugBoundingBox = !Minecraft.getMinecraft().renderManager.isDebugBoundingBox
            }
        }

        if (Keyboard.getEventKeyState()) {
                root.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        } else {
                root.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        }
    }

    @Throws(IOException::class)
    fun handleMouseInput() {
        val mouseX = Mouse.getEventX() * this.guiWidth() / Minecraft().displayWidth
        val mouseY = this.guiHeight() - Mouse.getEventY() * this.guiHeight() / Minecraft().displayHeight - 1
        val wheelAmount = Mouse.getEventDWheel()

        if (wheelAmount != 0) {
                root.mouseWheel(GuiComponentEvents.MouseWheelDirection.fromSign(wheelAmount))
        }
    }

    fun tick() {
        root.tick()
    }
}

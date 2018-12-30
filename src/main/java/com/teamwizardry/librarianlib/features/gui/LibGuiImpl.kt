package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.components.RootComponent
import com.teamwizardry.librarianlib.features.gui.layers.GradientLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Axis2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.awt.Color
import java.io.IOException

open class LibGuiImpl(protected val guiWidth: () -> Int, protected val guiHeight: () -> Int) {
    val root: RootComponent = RootComponent()
    val main: GuiComponent = object: GuiComponent(0, 0) {
        override var pos: Vec2d
            get() = vec(root.size.xi/2, root.size.yi/2)
            set(value) {}
        override var anchor: Vec2d
            get() = vec(0.5, 0.5)
            set(value) {}

        override fun layoutChildren() {
            val parentSize = this.parent!!.size - vec(20, 20)
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            val maxScale = scaledResolution.scaleFactor
            var scale = 1
            while((size.x / scale > parentSize.x || size.y / scale > parentSize.y) && scale < maxScale) {
                scale++
            }
            this.scale = 1.0/scale
        }
    }
    private val background = GradientLayer(Axis2d.Y,
        Color(0x10, 0x10, 0x10, 0xC0),
        Color(0x10, 0x10, 0x10, 0xD0),
        0, 0, 0, 0
    )
    var useDefaultBackground = false
        set(value) {
            field = value
            if(value && background.parent == null) {
                root.add(background)
            } else if(!value && background.parent != null) {
                root.remove(background)
            }
        }

    init {
        background.zIndex = Double.NEGATIVE_INFINITY
        main.disableMouseCollision = true
        root.disableMouseCollision = true
        root.add(main)
    }

    fun initGui() {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val resolution = vec(scaledResolution.scaledWidth, scaledResolution.scaledHeight)
        root.size_rm.set(resolution) //
        background.size = resolution
        main.setNeedsLayout()
    }

    fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.enableBlend()
        val relPos = vec(mouseX, mouseY)
        GlStateManager.pushMatrix()

        root.renderRoot(partialTicks, relPos)

        GlStateManager.popMatrix()
    }

    @Throws(IOException::class)
    fun mouseClicked(button: Int) {
            root.mouseDown(EnumMouseButton.getFromCode(button))
    }

    fun mouseReleased(button: Int) {
            root.mouseUp(EnumMouseButton.getFromCode(button))
    }

    @Throws(IOException::class)
    fun handleKeyboardInput() {
        if (Keyboard.getEventKeyState()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_D && GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()) {
                GuiLayer.isDebugMode = !GuiLayer.isDebugMode
            }
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

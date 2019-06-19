package com.teamwizardry.librarianlib.features.facade

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.components.StandaloneRootComponent
import com.teamwizardry.librarianlib.features.facade.layers.GradientLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.math.Axis2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.awt.Color
import java.io.IOException

open class LibGuiImpl(
    protected val guiWidth: () -> Int,
    protected val guiHeight: () -> Int,
    catchSafetyNet: (e: Exception) -> Unit
) {
    val root: StandaloneRootComponent = StandaloneRootComponent(catchSafetyNet)
    val safetyNet: Boolean by root::safetyNet.delegate
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

        initGui()
    }

    fun initGui() {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val resolution = vec(scaledResolution.scaledWidth, scaledResolution.scaledHeight)
        root.size_rm.set(resolution) //
        background.size = resolution
        main.setNeedsLayout()
        Keyboard.enableRepeatEvents(true)
    }

    fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val scaleFactor = ScaledResolution(Minecraft.getMinecraft()).scaleFactor
        GlStateManager.enableBlend()
        // convert logical to real position
        val relPos = vec(mouseX*scaleFactor, mouseY*scaleFactor)
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
        if (LibrarianLib.DEV_ENVIRONMENT && Keyboard.getEventKeyState()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_D && GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()) {
                GuiLayer.showDebugTilt = !GuiLayer.showDebugTilt
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_B && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                GuiLayer.showDebugBoundingBox = !GuiLayer.showDebugBoundingBox
            }
        }

        if(Keyboard.isRepeatEvent()) {
            root.keyRepeat(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        } else if (Keyboard.getEventKeyState()) {
            root.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        } else {
            root.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        }
    }

    @Throws(IOException::class)
    fun handleMouseInput() {
        val wheelAmount = Mouse.getEventDWheel()

        if (wheelAmount != 0) {
            root.mouseWheel(GuiComponentEvents.MouseWheelDirection.fromSign(wheelAmount))
        }
    }

    fun update() {
        root.update()
    }

    fun tick() {
        root.tick()
    }

    fun onClose() {
        Keyboard.enableRepeatEvents(false)
        Mouse.setNativeCursor(null)
    }
}

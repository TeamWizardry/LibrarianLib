package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.gui.EnumMouseButton
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.gui.component.GuiDrawContext
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.component.supporting.StencilUtil
import com.teamwizardry.librarianlib.math.CoordinateSpace2D
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.ScreenSpace
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11

open class StandaloneRootComponent(val closeGui: (Exception) -> Unit): RootComponent(0, 0, 0, 0) {
    /**
     * Set to true to enable the "safety net". When enabled, upon an exception the GUI will be closed instead of
     * crashing the game and a best-effort cleanup will be done (ending in-progress BufferBuilder, etc.).
     *
     * This component will attempt any cleanup necessary and will pass the exception to the [closeGui] callback
     * provided. The callback should handle how to present this error to the user/developer.
     *
     * On by default in development environments, disabled by default elsewhere.
     */
    var safetyNet: Boolean = true
    /**
     * Set to true to change the native cursor based on the top mouse hit
     */
    var enableNativeCursor: Boolean = true

    override var size: Vec2d
        get() = super.size
        set(value) {}
    override var pos: Vec2d
        get() = super.pos
        set(value) {}
    override var translateZ: Double
        get() = super.translateZ
        set(value) {}
    override var scale2d: Vec2d
        get() {
            val scaleFactor = Client.guiScaleFactor
            return vec(scaleFactor, scaleFactor)
        }
        set(value) {}
    override var rotation: Double
        get() = super.rotation
        set(value) {}
    override var anchor: Vec2d
        get() = super.anchor
        set(value) {}

    override val parentSpace: CoordinateSpace2D?
        get() = ScreenSpace
    override val parent: GuiComponent?
        get() = null

    override fun canAddToParent(parent: GuiLayer): Boolean {
        return false
    }

    private var currentTooltip: GuiLayer? = null

    fun renderRoot(partialTicks: Float, mousePos: Vec2d) {
    }

    override fun setNeedsLayout() = net { super.setNeedsLayout() }

    override fun mouseDown(button: EnumMouseButton) = net { super.mouseDown(button) }

    override fun mouseUp(button: EnumMouseButton) = net { super.mouseUp(button) }

    override fun keyRepeat(key: Char, keyCode: Int) = net { super.keyRepeat(key, keyCode) }

    override fun keyPressed(key: Char, keyCode: Int) = net { super.keyPressed(key, keyCode) }

    override fun keyReleased(key: Char, keyCode: Int) = net { super.keyReleased(key, keyCode) }

    override fun mouseWheel(amount: Double) = net { super.mouseWheel(amount) }

    override fun update() = net { super.update() }

    override fun tick() = net { super.tick() }

    private inline fun net(c: () -> Unit) {
        try {
            c()
        } catch (e: Exception) {
            if (!safetyNet) throw e else closeGui(e)
        }
    }

}
package com.teamwizardry.librarianlib.features.facade.components

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.facade.EnumMouseButton
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryTooltip
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.CoordinateSpace2D
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.input.Mouse
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
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            return vec(scaledResolution.scaleFactor, scaledResolution.scaleFactor)
        }
        set(value) {}
    override var rotation: Double
        get() = super.rotation
        set(value) {}
    override var anchor: Vec2d
        get() = super.anchor
        set(value) {}
    override var contentsOffset: Vec2d
        get() = super.contentsOffset
        set(value) {}

    override val parentSpace: CoordinateSpace2D?
        get() = ScreenSpace
    override val parent: GuiComponent?
        get() = null

    override fun glApplyTransform(inverse: Boolean) {
        if (GuiLayer.showDebugTilt) {
            if(inverse) {
                GlStateManager.translate(size.x / 2.0, size.y / 2.0, 0.0)
                GlStateManager.rotate(20f, 0f, 1f, 0f)
                GlStateManager.rotate(20f, 1f, 0f, 0f)
                GlStateManager.translate(-size.x / 2.0, -size.y / 2.0, 0.0)
            } else {
                GlStateManager.translate(size.x / 2.0, size.y / 2.0, 0.0)
                GlStateManager.rotate(-20f, 1f, 0f, 0f)
                GlStateManager.rotate(-20f, 0f, 1f, 0f)
                GlStateManager.translate(-size.x / 2.0, -size.y / 2.0, 0.0)
            }
        }
    }
    override fun glApplyContentsOffset(inverse: Boolean) {
        // nop
    }

    override fun canAddToParent(parent: GuiLayer): Boolean {
        return false
    }

    private var currentTooltip: PastryTooltip? = null

    fun renderRoot(partialTicks: Float, mousePos: Vec2d) {
        StencilUtil.clear()
        GL11.glEnable(GL11.GL_STENCIL_TEST)

        try {
            sortChildren()

            runLayoutIfNeeded()
            callPreFrame()

            topMouseHit = null
            updateMouse(mousePos)
            updateHits(this, 0.0)
            propagateHits()

            val tooltip = topMouseHit?.component?.tooltipLayer
            if(tooltip != currentTooltip) {
                currentTooltip?.removeFromParent()
                currentTooltip = tooltip
                tooltip?.also { this.add(it) }
            }

            if(enableNativeCursor) {
                Mouse.setNativeCursor(topMouseHit?.cursor?.lwjglCursor)
            }
            renderLayer(partialTicks)
        } catch(e: Exception) {
            if(!safetyNet) throw e

            Mouse.setNativeCursor(null)
            val tess = Tessellator.getInstance()
            try {
                tess.buffer.finishDrawing()
            } catch(e2: IllegalStateException) {
                // the buffer wasn't mid-draw
            }

            closeGui(e)
        }

        GL11.glDisable(GL11.GL_STENCIL_TEST)
    }

    override fun setNeedsLayout() = net { super.setNeedsLayout() }

    override fun mouseDown(button: EnumMouseButton) = net { super.mouseDown(button) }

    override fun mouseUp(button: EnumMouseButton) = net { super.mouseUp(button) }

    override fun keyRepeat(key: Char, keyCode: Int) = net { super.keyRepeat(key, keyCode) }

    override fun keyPressed(key: Char, keyCode: Int) = net { super.keyPressed(key, keyCode) }

    override fun keyReleased(key: Char, keyCode: Int) = net { super.keyReleased(key, keyCode) }

    override fun mouseWheel(direction: GuiComponentEvents.MouseWheelDirection) = net { super.mouseWheel(direction) }

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
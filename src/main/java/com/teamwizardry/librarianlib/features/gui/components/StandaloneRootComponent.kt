package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.supporting.MouseHit
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.CoordinateSpace2D
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11

open class StandaloneRootComponent: RootComponent(0, 0, 0, 0) {
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
        get() = super.scale2d
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
        if (GuiLayer.isDebugMode) {
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

    fun renderRoot(partialTicks: Float, mousePos: Vec2d) {
        StencilUtil.clear()
        GL11.glEnable(GL11.GL_STENCIL_TEST)

        cleanUpChildren()
        sortChildren()

        runLayoutIfNeeded()
        callPreFrame()

        topMouseHit = null
        updateMouse(mousePos)
        updateHits(this, 0.0)
        propagateHits()

        Mouse.setNativeCursor(topMouseHit?.cursor?.lwjglCursor)
        renderLayer(partialTicks)

        GL11.glDisable(GL11.GL_STENCIL_TEST)
    }
}
package com.teamwizardry.librarianlib.features.gui.debugger

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11.GL_ALWAYS
import org.lwjgl.opengl.GL11.GL_LEQUAL
import java.awt.Color

/**
 * Manages the debug panel
 */
class ComponentDebugger : GuiComponent(0, 0, 0, 0) {
    val debugPanel = ComponentDebugPanel()
    /** Used to make the resize bar positioned relative to the bottom of the screen so dragging works good */
    val resizeBarParent = ComponentVoid(0, 0)
    val resizeBar = ComponentRect(0, -8, 0, 8)
    /** Flattens the depth buffer before drawing, allowing us to draw over the other GUI */
    val flatten = ComponentRect(0, 0, 0, 0)

    init {
        // move the flattened plane back a bit so we are above it
        flatten.transform.translateZ = -1.0

        resizeBar.color.setValue(Color(0xA6A6A6))
        add(debugPanel)
        add(resizeBarParent)
        resizeBarParent.add(resizeBar)
        resizeBar.pos -= vec(0, debugPanel.size.y)

        DragMixin(resizeBar) {
            vec(0, it.y.clamp(-size.y, -resizeBar.size.y))
        }
        resizeBar.BUS.hook(DragMixin.DragMoveEvent::class.java) { e ->
            debugPanel.size = vec(debugPanel.size.x, -(e.newPos.y + 8))
        }
        resizeBar.render.hoverCursor = LibCursor.RESIZE_UPDOWN
    }

    /**
     * Flatten the depth buffer before drawing so we will be able to draw over everything else
     */
    @Hook
    @Suppress("UNUSED_PARAMETER")
    fun flattenDepth(e: GuiComponentEvents.PreDrawEvent) {
        GlStateManager.depthFunc(GL_ALWAYS)
        GlStateManager.colorMask(false, false, false, false)
        flatten.draw(Vec2d.ZERO, 0f)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.depthFunc(GL_LEQUAL)
    }

    /**
     * Update the sizes and positions of our subcomponents
     */
    @Hook
    @Suppress("UNUSED_PARAMETER")
    fun tick(e: GuiComponentEvents.ComponentTickEvent) {
        // resize the panel to the full width
        debugPanel.pos = vec(0, Math.max(0.0, size.y - debugPanel.size.y))
        debugPanel.size = vec(size.x, Math.min(size.y, debugPanel.size.y))

        // resize the resize handle to the full width and position it above the debug panel
        resizeBarParent.pos = vec(0, size.y)
        resizeBar.size = vec(size.x, resizeBar.size.y)

        flatten.size = size
    }
}

package com.teamwizardry.librarianlib.features.gui.windows

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.component.supporting.MouseHit
import com.teamwizardry.librarianlib.features.gui.component.supporting.compareTo
import com.teamwizardry.librarianlib.features.gui.components.RootComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

open class GuiWindow(width: Int, height: Int): RootComponent(0, 0, width, height) {
    var windowManager: IWindowManager? = null
    /**
     * True if this window is currently in focus
     */
    val isFocused: Boolean
        get() = windowManager?.isFocused(this) ?: false

    /**
     * Set to true to make this window float above others.
     */
    var isFloating: Boolean = false
        set(value) {
            field = value
            windowManager?.sort()
        }
    var shouldPauseGame: Boolean = true

    final override var mouseHit: MouseHit? = null
        private set
    final override var mouseOver: Boolean = false
        private set
    private var hadMouseHit = false

    init {
        this.BUS.hook<GuiLayerEvents.PostDrawEvent> {
            GlStateManager.depthFunc(GL11.GL_ALWAYS)
            GlStateManager.colorMask(false, false, false, false)
            val minX = 0.0
            val minY = 0.0
            val maxX = size.xi.toDouble()
            val maxY = size.yi.toDouble()

            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer

            GlStateManager.disableTexture2D()

            GlStateManager.enableBlend()

            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            vb.pos(minX, minY, 0.0).endVertex()
            vb.pos(minX, maxY, 0.0).endVertex()
            vb.pos(maxX, maxY, 0.0).endVertex()
            vb.pos(maxX, minY, 0.0).endVertex()
            tessellator.draw()

            GlStateManager.enableTexture2D()
            GlStateManager.colorMask(true, true, true, true)
            GlStateManager.depthFunc(GL11.GL_LEQUAL)
        }
    }

    fun requestFocus() = windowManager?.requestFocus(this) ?: false
    fun close() = windowManager?.close(this) ?: false

    /**
     * Open the window and place it at the center of the screen
     */
    fun open() {
        val existingGui = Minecraft().currentScreen
        val gui =
            if(existingGui is GuiWindowManager)
                existingGui
            else
                GuiWindowManager(existingGui).also { Minecraft().displayGuiScreen(it) }
        this.pos = gui.root.size/2 - this.size/2
        gui.open(this)
    }

    override fun updateHits(root: RootComponent, parentZ: Double) {
        this.topMouseHit = null
        for(child in subComponents) {
            if(child.isVisible) {
                child.updateHits(this, 0.0)
            }
        }

        this.mouseHit = null
        val zIndex = parentZ + zIndex
        if(!disableMouseCollision && isPointInBounds(mousePos)) {
            val mouseHit = MouseHit(this, zIndex, this.cursor ?: this.topMouseHit?.cursor)
            this.mouseHit = mouseHit
            if(isOpaqueToMouse && mouseHit > root.topMouseHit) {
                root.topMouseHit = mouseHit
            }
        }
    }

    override fun propagateHits() {
        val wasMouseOver = mouseOver

        val mouseHit = this.mouseHit
        val topHit = (this.parent?.root as? RootComponent)?.topMouseHit

        this.mouseOver = mouseHit != null && mouseHit >= topHit

        if(wasMouseOver && !mouseOver) {
            if(pressedButtons.isNotEmpty())
                BUS.fire(GuiComponentEvents.MouseDragLeaveEvent())
            BUS.fire(GuiComponentEvents.MouseLeaveEvent())
        } else if(!wasMouseOver && mouseOver) {
            if(pressedButtons.isNotEmpty())
                BUS.fire(GuiComponentEvents.MouseDragEnterEvent())
            BUS.fire(GuiComponentEvents.MouseEnterEvent())
        }

        if(hadMouseHit && mouseHit == null) {
            if(pressedButtons.isNotEmpty())
                BUS.fire(GuiComponentEvents.MouseDragOutEvent())
            BUS.fire(GuiComponentEvents.MouseMoveOutEvent())
        } else if(!hadMouseHit && mouseHit != null) {
            if(pressedButtons.isNotEmpty())
                BUS.fire(GuiComponentEvents.MouseDragInEvent())
            BUS.fire(GuiComponentEvents.MouseMoveInEvent())
        }

        if(!mouseOver)
            this.topMouseHit = MouseHit(this, Double.POSITIVE_INFINITY, null)
        for(child in subComponents) {
            child.propagateHits()
        }
    }

    override fun keyPressed(key: Char, keyCode: Int) {
        if(isFocused) super.keyPressed(key, keyCode)
    }

    override fun keyReleased(key: Char, keyCode: Int) {
        if(isFocused) super.keyReleased(key, keyCode)
    }

    override fun mouseDown(button: EnumMouseButton) {
        windowManager?.requestFocus(this)
        super.mouseDown(button)
    }

    override fun mouseUp(button: EnumMouseButton) {
        super.mouseUp(button)
    }

    class GainFocusEvent: Event()
    class LoseFocusEvent: Event()
    class CloseEvent: Event()
    class OpenEvent: Event()
}
package com.teamwizardry.librarianlib.facade

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.layer.supporting.StencilUtil
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.FacadeDebugOptions
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.client.gui.screen.Screen
import org.lwjgl.glfw.GLFW
import java.util.function.Predicate

/**
 * The abstract Facade GUI implementation.
 *
 * If the passed [screen] implements [FacadeMouseMask], any root layers with zIndex < 1000 will be masked.
 */
public open class FacadeWidget(
    private val screen: Screen
) {
    public val root: GuiLayer = GuiLayer()
    public val main: GuiLayer = GuiLayer()
    private val tooltipContainer = GuiLayer()
    private var currentTooltip: GuiLayer? = null

    /**
     * Whether F3 is currently pressed. Used to trigger F3+_ debug shortcuts
     */
    private var isF3Pressed: Boolean = false
    /**
     * Set to true when F3 is pressed, then set to false if a shortcut (e.g. F3+B) is pressed. If F3 is released with
     * this set to true, the configurator is opened
     */
    private var isOpeningDebugConfigurator: Boolean = false
    private val debugConfigurator = FacadeDebugOptionsConfigurator(debugOptions)

    init {
        root.ignoreMouseOverBounds = true
        root.add(main, tooltipContainer)
        tooltipContainer.zIndex = 100_000.0
        tooltipContainer.interactive = false
    }

    /**
     * We keep track of the mouse position ourselves both so we can provide deltas for move events and so we can provide
     * subpixel mouse positions in [render]
     */
    private var mousePos: Vec2d = Vec2d.ZERO

    /**
     * The mouse hit for the current mouse position
     */
    public var mouseHit: MouseHit = MouseHit(null, false, Vec2d.ZERO)
        private set

    //region Delegates
    public fun mouseMoved(xPos: Double, yPos: Double) {
        if (debugConfigurator.isOpen) {
            debugConfigurator.mouseMoved(xPos, yPos)
            return
        }
        val pos = vec(xPos, yPos)
        computeMouseOver(pos)
        safetyNet("firing a MouseMove event") {
            root.triggerEvent(GuiLayerEvents.MouseMove(pos, mousePos))
        }
        mousePos = pos
    }

    public fun mouseClicked(xPos: Double, yPos: Double, button: Int) {
        if (debugConfigurator.isOpen) {
            debugConfigurator.mouseClicked(xPos, yPos, button)
            return
        }
        val pos = vec(xPos, yPos)
        computeMouseOver(pos)
        safetyNet("firing a MouseDown event") {
            root.triggerEvent(GuiLayerEvents.MouseDown(pos, button))
        }
    }

    public fun mouseReleased(xPos: Double, yPos: Double, button: Int) {
        if (debugConfigurator.isOpen) return
        val pos = vec(xPos, yPos)
        computeMouseOver(pos)
        safetyNet("firing a MouseUp event") {
            root.triggerEvent(GuiLayerEvents.MouseUp(pos, button))
        }
    }

    public fun mouseScrolled(xPos: Double, yPos: Double, deltaY: Double) {
        if (debugConfigurator.isOpen) return
        val pos = vec(xPos, yPos)
        val delta = vec(0.0, deltaY)
        computeMouseOver(pos)
        safetyNet("firing a MouseScroll event") {
            root.triggerEvent(GuiLayerEvents.MouseScroll(pos, delta))
        }
    }

    public fun mouseDragged(xPos: Double, yPos: Double, button: Int, deltaX: Double, deltaY: Double) {
        if (debugConfigurator.isOpen) return
        val pos = vec(xPos, yPos)
        val lastPos = vec(xPos - deltaX, yPos - deltaY)
        computeMouseOver(pos)
        safetyNet("firing a MouseDrag event") {
            root.triggerEvent(GuiLayerEvents.MouseDrag(pos, lastPos, button))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    public fun changeFocus(reverse: Boolean) {
        // todo
    }

    public fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) {
        when (keyCode) {
            GLFW.GLFW_KEY_F3 -> {
                isF3Pressed = true
                if (debugConfigurator.isOpen) {
                    debugConfigurator.isOpen = false
                } else {
                    isOpeningDebugConfigurator = true
                }
            }
            GLFW.GLFW_KEY_ESCAPE -> {
                if (debugConfigurator.isOpen) {
                    debugConfigurator.isOpen = false
                } else {
                    screen.onClose()
                }
            }
        }
        if (isF3Pressed) {
            if (debugConfigurator.shortcutKeyPressed(keyCode)) {
                isOpeningDebugConfigurator = false
            }
        }

        if (debugConfigurator.isOpen) return

        safetyNet("firing a KeyDown event") {
            root.triggerEvent(GuiLayerEvents.KeyDown(keyCode, scanCode, modifiers))
        }
    }

    public fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (keyCode == GLFW.GLFW_KEY_F3) {
            isF3Pressed = false
            if(isOpeningDebugConfigurator) {
                isOpeningDebugConfigurator = false
                debugConfigurator.isOpen = true
            }
        }
        if (debugConfigurator.isOpen) return
        safetyNet("firing a KeyUp event") {
            root.triggerEvent(GuiLayerEvents.KeyUp(keyCode, scanCode, modifiers))
        }
    }

    public fun charTyped(codepoint: Char, modifiers: Int) {
        if (debugConfigurator.isOpen) return
        safetyNet("firing a CharTyped event") {
            root.triggerEvent(GuiLayerEvents.CharTyped(codepoint, modifiers))
        }
    }

    public fun removed() {
        Cursor.setCursor(null)
    }
    //endregion

    private fun computeMouseOver(absolute: Vec2d) {
        safetyNet("computing the mouse position") {
            lastHit = null
            mouseHit = computeMouseHit(absolute, true)
            generateSequence(mouseHit.layer) { if (it.propagatesMouseOver) it.parent else null }.forEach {
                it.mouseOver = true
            }
        }
    }

    private fun computeMouseHit(absolute: Vec2d, isMousePos: Boolean): MouseHit {
        var hitLayer = root.hitTest(absolute, Matrix3dStack(), isMousePos)
        val rootZ = hitLayer?.let { over ->
            root.children.find {
                over == it || over in it
            }
        }?.zIndex ?: .0
        val isAboveVanilla = rootZ >= 1000
        if (screen is FacadeMouseMask) {
            if (!isAboveVanilla && screen.isMouseMasked(
                    absolute.x,
                    absolute.y
                )
            ) {
                hitLayer = null
            }
        }
        return MouseHit(hitLayer, isAboveVanilla, absolute)
    }

    private var lastHit: MouseHit? = null

    /**
     * Perform a hit test on the layer hierarchy. This method is optimized for repeated accesses with the same position.
     * If the passed position is identical to the last passed position it will reuse the result. This cache is reset
     * when real mouse position is recalculated.
     *
     * @param xPos the absolute x coordinate to test in logical pixels
     * @param yPos the absolute y coordinate to test in logical pixels
     */
    public fun hitTest(xPos: Double, yPos: Double): MouseHit {
        val pos = vec(xPos, yPos)
        lastHit?.also {
            if (pos == it.pos)
                return it
        }
        return computeMouseHit(pos, false).also { lastHit = it }
    }

    /**
     * Performs all the logical updates that occur on a frame, e.g. computing mouseover, etc. This is the first step in
     * rendering a frame.
     */
    public fun update() {
        safetyNet("updating") {
            root.pos = vec(0, 0)
            root.size = vec(Client.window.scaledWidth, Client.window.scaledHeight)
            main.pos = ((root.size - main.size) / 2).round()
            tooltipContainer.frame = root.bounds

            computeMouseOver(mousePos)
            var tooltip: GuiLayer? = null
            var cursor: Cursor? = null
            generateSequence(mouseHit.layer) { it.parent }.forEach {
                tooltip = tooltip ?: it.tooltipLayer
                cursor = cursor ?: it.cursor
            }

            if (tooltip != currentTooltip) {
                currentTooltip?.removeFromParent()
                currentTooltip = tooltip
                tooltip?.also { tooltipContainer.add(it) }
            }
            Cursor.setCursor(cursor)

            root.updateAnimations(Client.time.time)
            root.triggerEvent(GuiLayerEvents.Update())
            root.zSort()
            root.triggerEvent(GuiLayerEvents.PrepareLayout())
            root.runLayout()
            root.clearAllDirtyLayout()
        }
    }

    /**
     * The second step in rendering a frame. This can be split up into multiple passes using [filterRendering].
     */
    public fun render() {
        safetyNet("rendering") {
            StencilUtil.clear()
            StencilUtil.enable()
            RenderSystem.pushMatrix()
            val context = GuiDrawContext(Matrix3dStack(), debugOptions, false)
            root.renderLayer(context)
            RenderSystem.popMatrix()
            StencilUtil.disable()
        }
        if(debugConfigurator.isOpen) {
            debugConfigurator.render()
        }
    }

    /**
     * Configures the rendering system to only render children of the root layer that match the given predicate.
     * This is used in containers to render the foreground and background at separate times.
     */
    public fun filterRendering(filter: Predicate<GuiLayer>) {
        safetyNet("filtering rendering") {
            root.forEachChild {
                it.skipRender = !filter.test(it)
            }
        }
    }

    /**
     * The result of hit testing the layer hierarchy
     */
    public data class MouseHit(
        /**
         * The layer the mouse hit, if any
         */
        val layer: GuiLayer?,
        /**
         * True if the layer that was hit is above the vanilla GUI (its root has a zIndex >= 1000)
         */
        val isOverVanilla: Boolean,
        /**
         * The absolute position of the hit test in display pixels (not logical pixels)
         */
        val pos: Vec2d
    )

    public companion object {
        @JvmStatic
        public val debugOptions: FacadeDebugOptions = FacadeDebugOptions()
    }
}

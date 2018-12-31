package com.teamwizardry.librarianlib.features.gui.provided.pastry.windows

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.math.Vec2d

open class PastryWindow(width: Int, height: Int): PastryWindowBase(width, height) {
    val header = GuiComponent(0, 0, width, 0)
    val content = GuiComponent(0, 15, width, height)

    private val moveHandler = MoveHandler()

    init {
        header.heighti = 15
        content.clipToBounds = true
        add(header, content)
        addDragHooks(header)
    }

    override fun layoutChildren() {
        size = content.size + vec(0, header.height)
        header.pos = vec(0, 0)
        content.y = header.height
        header.width = width
        super.layoutChildren()
    }

    /**
     * Called when the window is about to move
     *
     * @property pos The window's current position
     * @property newPos What the window's position will be set to after this event
     */
    class WindowMoveEvent(val pos: Vec2d, var newPos: Vec2d) : Event()

    fun addDragHooks(component: GuiComponent) {
        moveHandler.addDragHooks(component)
    }

    private inner class MoveHandler {
        var draggingButton: EnumMouseButton? = null
        var clickedPoint = Vec2d.ZERO
        var previousPos = Vec2d.ZERO

        val window = this@PastryWindow

        fun addDragHooks(component: GuiComponent) {
            component.BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
                if (draggingButton == null && component.mouseOver) {
                    draggingButton = event.button
                    clickedPoint = component.mousePos
                    previousPos = component.pos
                    event.cancel()
                }
            }
            component.BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
                if (draggingButton == event.button) {
                    draggingButton = null
                    event.cancel()
                }
            }
        }

        init {
            window.BUS.hook(GuiComponentEvents.CalculateMousePositionEvent::class.java) { event ->
                val mouseButton = draggingButton
                if (mouseButton != null) {
                    val pinnedPoint = window.convertPointToParent(clickedPoint)
                    val offset = window.convertPointToParent(event.mousePos) - pinnedPoint
                    var validRect = window.parent?.bounds ?: Rect2d.INFINITE
                    validRect = Rect2d(validRect.pos, validRect.size - window.size)
                    val newPos = validRect.clamp(window.pos + offset)

                    if (newPos != window.pos) {
                        window.pos = window.BUS.fire(
                            WindowMoveEvent(window.pos, newPos)
                        ).newPos
                        event.mousePos = window.parentComponent?.let { parent ->
                            window.convertPointFromParent(parent.mousePos)
                        } ?: window.mousePos
                    }
                }
            }
        }
    }
}
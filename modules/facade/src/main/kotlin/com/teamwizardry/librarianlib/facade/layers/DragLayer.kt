package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.etcetera.eventbus.CancelableEvent
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.value.IMValueBoolean
import com.teamwizardry.librarianlib.math.Vec2d

/**
 * A layer that will move the [targetLayer] when the mouse is dragged on it. If the target is null this layer will move
 * itself.
 */
public class DragLayer : GuiLayer {
    public constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)
    public constructor(posX: Int, posY: Int): super(posX, posY)
    public constructor(): super()

    /**
     * The button that should activate the dragging action. Defaults to the left mouse button
     */
    public var button: Int = 0
        set(value) {
            if(field != value) {
                currentlyDragging = false
            }
            field = value
        }

    /**
     * The layer that should be moved by this layer. The target layer should be an ancestor of this layer, otherwise
     * the dragging will behave oddly. If the target is null this layer will move itself.
     */
    public var targetLayer: GuiLayer? = null

    /**
     * Whether the dragging is active.
     */
    public var active_im: IMValueBoolean = imBoolean(true)
    /**
     * Whether the dragging is active.
     */
    public var active: Boolean by active_im

    public class DragEvent(public var targetPosition: Vec2d): CancelableEvent()

    private var currentlyDragging: Boolean = false
    private var mouseDownPos: Vec2d = vec(0, 0)

    @Hook
    private fun mouseDown(e: GuiLayerEvents.MouseDown) {
        if(this.mouseOver && e.button == button) {
            currentlyDragging = true
            mouseDownPos = mousePos
        }
    }

    @Hook
    private fun mouseUp(e: GuiLayerEvents.MouseUp) {
        if(e.button == button) {
            currentlyDragging = false
        }
    }

    @Hook
    private fun mouseMove(e: GuiLayerEvents.MouseMove) {
        if(!currentlyDragging) return

        val localOffset = e.pos - mouseDownPos
        val target = this.targetLayer ?: this
        val targetParent = target.parent ?: return
        val targetPos = target.pos + this.convertOffsetTo(localOffset, targetParent)
        val event = DragEvent(targetPos)
        BUS.fire(event)
        if(!event.isCanceled()) {
            target.pos = event.targetPosition
        }
    }
}
package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.facade.pastry.ExperimentalPastryAPI
import com.teamwizardry.librarianlib.facade.value.RMValueDouble
import com.teamwizardry.librarianlib.math.Axis2d
import com.teamwizardry.librarianlib.math.Cardinal2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import kotlin.math.PI
import kotlin.math.max

/* TODO
@ExperimentalPastryAPI
class PastrySlider(posX: Int, posY: Int, length: Int, var pointed: Boolean, facing: Cardinal2d) : GuiLayer(
    posX, posY,
    if(facing.axis == Axis2d.Y) length else 7,
    if(facing.axis == Axis2d.X) length else 7
) {
    init {
        this.size_rm.locked = true
    }
    private val inner = GuiLayer(0, 0, length, 7)

    val leftLine = RectLayer(PastryTexture.sliderLinesColor, 3, 3, 0, 1)
    val rightLine = RectLayer(PastryTexture.sliderLinesColor, widthi-3, 3, 0, 1)

    var length: Int = length
        set(value) {
            field = value
            this.size_rm.locked = false
            size = vec(
                if(facing.axis == Axis2d.Y) length else 7,
                if(facing.axis == Axis2d.X) length else 7
            )
            this.size_rm.locked = true
            rightLine.x = length - 3.5
            inner.size = vec(length, 7)
        }
    var facing: Cardinal2d = facing
        set(value) {
            val changed = field != value
            field = value
            if(changed) {
                updateFacing()
            }
        }

    var range: ClosedRange<Double> = 0.0 .. 1.0
        set(value) {
            val changed = field != value
            field = value
            if(changed) this.layoutChildren()
        }
    val value_rm: RMValueDouble = RMValueDouble(0.0) { oldValue, newValue ->
        if(oldValue != newValue) this.markLayoutDirty()
    }
    var value: Double by value_rm

    private val handleLayer = SpriteLayer(PastryTexture.sliderHandle, 0, 0, 7, 7)

    init {
        handleLayer.componentWrapper().cursor = LibCursor.POINT

        handleLayer.anchor = vec(0.5, 0.5)
        inner.add(handleLayer.componentWrapper())

        leftLine.x = 3.5
        rightLine.anchor = vec(1, 0)
        inner.add(leftLine, rightLine)

        DragMixin(handleLayer.componentWrapper()) { it }

        handleLayer.BUS.hook<DragMixin.DragMoveEvent> { event ->
            mouseAdjustValue(event.newPos)
            event.newPos = posFromValue(value)
            setNeedsLayout()
        }

        inner.BUS.hook<GuiLayerEvents.MouseDrag> {
            if (mouseOver) {
                mouseAdjustValue(mousePos)
                setNeedsLayout()
            }
        }

        inner.BUS.hook<GuiLayerEvents.MouseClick> {
            if (mouseOver) {
                mouseAdjustValue(mousePos)
                setNeedsLayout()
            }
        }

        this.add(inner)

        updateFacing()
    }

    fun updateFacing() {
        length = length // update size
        when(facing) {
            Cardinal2d.UP -> { // up
                inner.anchor = vec(1, 1)
                inner.rotation = 2*PI
                handleLayer.sprite = PastryTexture.sliderHandleUp
                handleLayer.rotation = 0.0
            }
            Cardinal2d.DOWN -> { // down
                inner.anchor = vec(0, 0)
                inner.rotation = 0.0
                handleLayer.sprite = PastryTexture.sliderHandleDown
                handleLayer.rotation = 0.0
            }
            Cardinal2d.LEFT -> { // left
                inner.anchor = vec(0, 1)
                inner.rotation = PI
                handleLayer.sprite = PastryTexture.sliderHandleLeft
                handleLayer.rotation = 0.0
            }
            Cardinal2d.RIGHT -> { // right
                inner.anchor = vec(1, 0)
                inner.rotation = -PI
                handleLayer.sprite = PastryTexture.sliderHandleRight
                handleLayer.rotation = 0.0
            }
        }
        if(!pointed) {
            handleLayer.sprite = PastryTexture.sliderHandle
            handleLayer.rotation = -inner.rotation
        }
        this.setNeedsLayout()
    }

    private fun mouseAdjustValue(mousePos: Vec2d) {
        val newValue = snapValue(valueFromPos(mousePos)).clamp(range.start, range.endInclusive)
        this.value = BUS.fire(ValueChangeEvent(this.value, newValue)).newValue
    }

    private fun snapValue(value: Double): Double {
        return value
    }

    private fun valueFromPos(mousePos: Vec2d): Double {
        val fraction = ((mousePos.x - 3.5) / (inner.width - 7)).clamp(0.0, 1.0)
        return range.start + fraction * (range.endInclusive - range.start)
    }

    private fun posFromValue(value: Double): Vec2d {
        if(range.endInclusive == range.start) return vec(3.5, 3.5)
        val fraction = (value - range.start) / (range.endInclusive - range.start)
        return vec(
            fraction * (inner.width - 7) + 3.5,
            3.5
        )
    }

    override fun layoutChildren() {
        handleLayer.pos = posFromValue(value)
        val handleX = handleLayer.x - 3.5
        val trackWidth = inner.width - 7
        leftLine.width = max(0.0, handleX - 4)
        rightLine.width = max(0.0, trackWidth - handleX - 4)
    }

    /**
     * Called before the value is changes so the new value can be adjusted
     */
    class ValueChangeEvent(val oldValue: Double, var newValue: Double): Event()
}
 */
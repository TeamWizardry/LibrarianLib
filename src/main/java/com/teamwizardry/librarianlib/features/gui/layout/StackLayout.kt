package com.teamwizardry.librarianlib.features.gui.layout

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.ceilInt
import com.teamwizardry.librarianlib.features.kotlin.floorInt
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

/**
 * A simple stack layout, useful when [Flexbox] is overkill.
 *
 * The layout goes like this:
 * - if [horizontal] is true, line up the children of this layer along the x axis going left-to-right
 * (or right-to-left if [reverse] is true)
 * - if [horizontal] is false, line up the children of this layer along the y axis going top-to-bottom
 * (or bottom-to-top if [reverse] is true)
 *
 * Position the layers within this one using [align]. Note that this layer doesn't need to have a size and doesn't
 * resize to fit its contents. The layout is based on the child's [frame][GuiLayer.frame], meaning it takes into
 * account scaling, rotation, and the anchor.
 *
 *
 * The [build] static methods are provided to concisely build a stack layout.
 */
class StackLayout(posX: Int, posY: Int, width: Int, height: Int,
    var horizontal: Boolean,
    var align: Align2d,
    var reverse: Boolean,
    var spacing: Double
): GuiLayer(posX, posY, width, height) {

    fun fitChildren() {
        this.fitLength()
        this.fitBredth()
    }

    fun fitLength() {
        if(children.isEmpty()) {
            if(horizontal)
                width = 0.0
            else
                height = 0.0
            return
        }
        var length = 0.0
        children.forEach { child ->
            val frame = child.frame
            if(horizontal) {
                length += frame.width + spacing
            } else {
                length += frame.height + spacing
            }
        }
        length -= spacing
        if(horizontal)
            width = length
        else
            height = length
    }

    fun fitBredth() {
        var bredth = 0.0
        children.forEach { child ->
            val frame = child.frame
            if(horizontal) {
                bredth = max(bredth, frame.height)
            } else {
                bredth = max(bredth, frame.width)
            }
        }
        if(horizontal)
            height = bredth
        else
            width = bredth
    }


    override fun layoutChildren() {
        super.layoutChildren()

        var accumulator = 0.0
        val positions = children.map { child ->
            accumulator.also {
                if (horizontal) {
                    accumulator += child.frame.width + spacing
                } else {
                    accumulator += child.frame.height + spacing
                }
            }
        }

        if(horizontal) {
            val xOffset = when (align.x) {
                Align2d.X.LEFT -> 0.0
                Align2d.X.CENTER -> floor((width-accumulator)/2)
                Align2d.X.RIGHT -> ceil(width-accumulator)
            }

            children.zip(positions).forEach { (child, position) ->
                val frame = child.frame
                val x = position + xOffset
                val y = when(align.y) {
                    Align2d.Y.TOP -> 0
                    Align2d.Y.CENTER -> floorInt((height - frame.height)/2)
                    Align2d.Y.BOTTOM -> ceilInt(height - frame.height)
                }

                child.frame = Rect2d(vec(x, y), frame.size)
            }
        } else {
            val yOffset = when (align.y) {
                Align2d.Y.TOP -> 0.0
                Align2d.Y.CENTER -> floor((height-accumulator)/2)
                Align2d.Y.BOTTOM -> ceil(height-accumulator)
            }

            children.zip(positions).forEach { (child, position) ->
                val frame = child.frame
                val y = position + yOffset
                val x = when(align.x) {
                    Align2d.X.LEFT -> 0
                    Align2d.X.CENTER -> floorInt((width - frame.width)/2)
                    Align2d.X.RIGHT -> ceilInt(width - frame.width)
                }

                child.frame = Rect2d(vec(x, y), frame.size)
            }
        }
    }

    companion object {
        fun build(): Builder {
            return Builder(0.0, 0.0)
        }

        fun build(posX: Int, posY: Int): Builder {
            return Builder(posX.toDouble(), posY.toDouble())
        }

        fun build(pos: Vec2d): Builder {
            return Builder(pos.x, pos.y)
        }
    }

    /**
     * Defaults:
     * - posX: passed value
     * - posY: passed value
     * - width: 0
     * - height: 0
     * - horizontal: false
     * - align: [TOP_LEFT][Align2d.TOP_LEFT]
     * - reverse: false
     * - spacing: 0.0
     */
    class Builder(private var posX: Double, private var posY: Double) {
        private var width: Double = 0.0
        private var height: Double = 0.0
        private var horizontal: Boolean = false
        private var align: Align2d = Align2d.TOP_LEFT
        private var reverse: Boolean = false
        private var spacing: Double = 0.0
        private var children: MutableList<GuiLayer> = mutableListOf()
        private var fitLength: Boolean = false
        private var fitBredth: Boolean = false

        /** Stack horizontally */
        fun horizontal() = build {
            horizontal = true
        }

        /** Stack vertically */
        fun vertical() = build {
            horizontal = false
        }

        /** Set alignment */
        fun align(alignment: Align2d) = build {
            align = alignment
        }

        /** Set x alignment */
        fun alignRight() = build {
            align = Align2d.get(Align2d.X.RIGHT, align.y)
        }

        /** Set x alignment */
        fun alignCenterX() = build {
            align = Align2d.get(Align2d.X.CENTER, align.y)
        }

        /** Set x alignment */
        fun alignLeft() = build {
            align = Align2d.get(Align2d.X.LEFT, align.y)
        }

        /** Set y alignment */
        fun alignTop() = build {
            align = Align2d.get(align.x, Align2d.Y.TOP)
        }

        /** Set y alignment */
        fun alignCenterY() = build {
            align = Align2d.get(align.x, Align2d.Y.CENTER)
        }

        /** Set y alignment */
        fun alignBottom() = build {
            align = Align2d.get(align.x, Align2d.Y.BOTTOM)
        }

        /** Enable reverse */
        fun reverse() = build {
            reverse = true
        }

        /** Set position */
        fun pos(posX: Int, posY: Int) = build {
            this.posX = posX.toDouble()
            this.posY = posY.toDouble()
        }

        /** Set position */
        fun pos(posX: Double, posY: Double) = build {
            this.posX = posX
            this.posY = posY
        }

        /** Set position */
        fun pos(pos: Vec2d) = build {
            this.posX = pos.x
            this.posY = pos.y
        }

        /** Set size */
        fun size(width: Int, height: Int) = build {
            this.width = width.toDouble()
            this.height = height.toDouble()
        }

        /** Set size */
        fun size(width: Double, height: Double) = build {
            this.width = width
            this.height = height
        }

        /** Set size */
        fun size(size: Vec2d) = build {
            this.width = size.x
            this.height = size.y
        }

        /** Set width */
        fun width(width: Int) = build {
            this.width = width.toDouble()
        }

        /** Set width */
        fun width(width: Double) = build {
            this.width = width
        }

        /** Set height */
        fun height(height: Int) = build {
            this.height = height.toDouble()
        }

        /** Set height */
        fun height(height: Double) = build {
            this.height = height
        }

        /** Set spacing */
        fun space(spacing: Int) = build {
            this.spacing = spacing.toDouble()
        }

        /** Set spacing */
        fun space(spacing: Double) = build {
            this.spacing = spacing
        }

        /** Add the passed layers to the stack when complete */
        fun add(vararg children: GuiLayer) = build {
            this.children.addAll(children)
        }

        /** fit children along the primary axis when complete */
        fun fitLength() = build {
            this.fitLength = true
        }

        /** fit children along the crosswise axis when complete */
        fun fitBredth() = build {
            this.fitBredth = true
        }

        /** fit children along both axes when complete */
        fun fit() = build {
            this.fitLength = true
            this.fitBredth = true
        }

        private fun buildLayer(): StackLayout {
            val stack = StackLayout(
                0, 0, 0, 0,
                horizontal, align, reverse, spacing
            )
            stack.pos = vec(posX, posY)
            stack.size = vec(width, height)
            return stack
        }

        fun layer(): StackLayout {
            val layer = buildLayer()
            layer.add(*children.toTypedArray())
            if(fitLength) {
                layer.fitLength()
            }
            if(fitBredth) {
                layer.fitBredth()
            }
            return layer
        }

        fun component(): GuiComponent {
            val layer = buildLayer()
            val component = layer.componentWrapper()
            component.add(*children.toTypedArray())
            if(fitLength) {
                layer.fitLength()
            }
            if(fitBredth) {
                layer.fitBredth()
            }
            return component
        }

        private inline fun build(func: () -> Unit): Builder {
            func()
            return this
        }
    }
}
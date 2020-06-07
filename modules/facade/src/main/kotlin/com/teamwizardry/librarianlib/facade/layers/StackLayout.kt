package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.math.Align2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.ceilInt
import com.teamwizardry.librarianlib.math.floorInt
import com.teamwizardry.librarianlib.math.rect
import com.teamwizardry.librarianlib.math.vec
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

/**
 * A simple stack layout, useful for lists, which Yoga layout is less than elegant at handling (for example, Yoga has no
 * spacing property).
 *
 * The layout goes like this:
 * - if [horizontal] is true, line up the children of this layer along the x axis going left-to-right
 * (or right-to-left if [reverse] is true)
 * - if [horizontal] is false, line up the children of this layer along the y axis going top-to-bottom
 * (or bottom-to-top if [reverse] is true)
 *
 * Position the layers within this one using [align]. Note that this layer doesn't need to have a size and doesn't
 * resize to fit its contents. The layout is based on the child's [frame][GuiLayer.frame], meaning it takes into
 * account scaling, rotation, and the anchor. If [preserveCrosswise] is true the crosswise align (y if horizontal, x
 * if vertical) will be ignored and children's positions along that axis will not be modified.
 *
 * The [build] static methods are provided to concisely build a stack layout.
 */
class StackLayout(
    posX: Int, posY: Int, width: Int, height: Int,
    var horizontal: Boolean,
    var align: Align2d,
    var reverse: Boolean,
    var spacing: Double,
    var collapseInvisible: Boolean,
    var preserveCrosswise: Boolean
): GuiLayer(posX, posY, width, height) {
    companion object {
        fun build(): StackLayoutBuilder {
            return StackLayoutBuilder(0.0, 0.0)
        }

        fun build(posX: Int, posY: Int): StackLayoutBuilder {
            return StackLayoutBuilder(posX.toDouble(), posY.toDouble())
        }

        fun build(pos: Vec2d): StackLayoutBuilder {
            return StackLayoutBuilder(pos.x, pos.y)
        }
    }

    fun fitToLength() {
        if(children.isEmpty()) {
            if(horizontal)
                width = 0.0
            else
                height = 0.0
            return
        }
        var length = 0.0
        children.forEach { child ->
            if(collapseInvisible && !child.isVisible)
                return@forEach
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

    fun fitToBreadth() {
        var bredth = 0.0
        children.forEach { child ->
            if(collapseInvisible && !child.isVisible)
                return@forEach
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
        var accumulator = 0.0
        val positions = children.map { child ->
            accumulator.also {
                if(collapseInvisible && !child.isVisible)
                    return@also
                if (horizontal) {
                    accumulator += child.frame.width + spacing
                } else {
                    accumulator += child.frame.height + spacing
                }
            }
        }

        val reverseOffset = if(reverse) accumulator else 0.0
        if(horizontal) {
            val xOffset = when (align.x) {
                Align2d.X.LEFT -> 0.0 + reverseOffset
                Align2d.X.CENTER -> floor((width-accumulator)/2 + reverseOffset)
                Align2d.X.RIGHT -> ceil(width-accumulator + reverseOffset)
            }

            children.zip(positions).forEach { (child, position) ->
                val frame = child.frame
                val x = xOffset + if(reverse) -position else position
                val y = if(preserveCrosswise)
                    child.frame.minY.toInt()
                else when(align.y) {
                    Align2d.Y.TOP -> 0
                    Align2d.Y.CENTER -> floorInt((height - frame.height)/2)
                    Align2d.Y.BOTTOM -> ceilInt(height - frame.height)
                }

                child.frame = rect(if(reverse) x-frame.width else x, y, frame.width, frame.height)
            }
        } else {
            val yOffset = when (align.y) {
                Align2d.Y.TOP -> 0.0 + reverseOffset
                Align2d.Y.CENTER -> floor((height-accumulator)/2 + reverseOffset)
                Align2d.Y.BOTTOM -> ceil(height-accumulator + reverseOffset)
            }

            children.zip(positions).forEach { (child, position) ->
                val frame = child.frame
                val y = yOffset + if(reverse) -position else position
                val x = if(preserveCrosswise)
                    child.frame.minX.toInt()
                else when(align.x) {
                    Align2d.X.LEFT -> 0
                    Align2d.X.CENTER -> floorInt((width - frame.width)/2)
                    Align2d.X.RIGHT -> ceilInt(width - frame.width)
                }

                child.frame = rect(x, if(reverse) y-frame.height else y, frame.width, frame.height)
            }
        }
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
 * - collapseInvisible: true
 * - preserveCrosswise: false
 */
class StackLayoutBuilder(private var posX: Double, private var posY: Double) {
    private var width: Double = 0.0
    private var height: Double = 0.0
    private var horizontal: Boolean = false
    private var align: Align2d = Align2d.TOP_LEFT
    private var reverse: Boolean = false
    private var spacing: Double = 0.0
    private var children: MutableList<GuiLayer> = mutableListOf()
    private var fitToLength: Boolean = false
    private var fitToBreadth: Boolean = false
    private var collapseInvisible: Boolean = true
    private var preserveCrosswise: Boolean = false

    /** Stack horizontally */
    fun horizontal() = build {
        horizontal = true
    }

    /** Stack vertically */
    fun vertical() = build {
        horizontal = false
    }

    fun preserveCrosswise() = build {
        preserveCrosswise = true
    }

    /** Set alignment. Disables [preserveCrosswise][StackLayout.preserveCrosswise] */
    fun align(alignment: Align2d) = build {
        align = alignment
    }

    /** Set x alignment */
    fun alignRight() = build {
        align = Align2d[Align2d.X.RIGHT, align.y]
    }

    /** Set x alignment */
    fun alignCenterX() = build {
        align = Align2d[Align2d.X.CENTER, align.y]
    }

    /** Set x alignment */
    fun alignLeft() = build {
        align = Align2d[Align2d.X.LEFT, align.y]
    }

    /** Set y alignment */
    fun alignTop() = build {
        align = Align2d[align.x, Align2d.Y.TOP]
    }

    /** Set y alignment */
    fun alignCenterY() = build {
        align = Align2d[align.x, Align2d.Y.CENTER]
    }

    /** Set y alignment */
    fun alignBottom() = build {
        align = Align2d[align.x, Align2d.Y.BOTTOM]
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
    fun spacing(spacing: Int) = build {
        this.spacing = spacing.toDouble()
    }

    /** Set spacing */
    fun spacing(spacing: Double) = build {
        this.spacing = spacing
    }

    /** Include invisible layers in layout calculations, keeping an empty spot for them */
    fun includeInvisible() = build {
        this.collapseInvisible = false
    }

    /** Add the passed layers to the stack when complete */
    fun add(vararg children: GuiLayer) = build {
        this.children.addAll(children)
    }

    /** fit children along the primary axis when complete */
    fun fitLength() = build {
        this.fitToLength = true
    }

    /** fit children along the crosswise axis when complete */
    fun fitBreadth() = build {
        this.fitToBreadth = true
    }

    /** fit children along both axes when complete */
    fun fit() = build {
        this.fitToLength = true
        this.fitToBreadth = true
    }

    fun build(): StackLayout {
        val layer = StackLayout(
            0, 0, 0, 0,
            horizontal, align, reverse, spacing, collapseInvisible, preserveCrosswise
        )
        layer.pos = vec(posX, posY) // constructor only accepts ints
        layer.size = vec(width, height) // ditto

        layer.add(*children.toTypedArray())
        if(fitToLength) {
            layer.fitToLength()
        }
        if(fitToBreadth) {
            layer.fitToBreadth()
        }
        return layer
    }

    private inline fun build(func: () -> Unit): StackLayoutBuilder {
        func()
        return this
    }
}

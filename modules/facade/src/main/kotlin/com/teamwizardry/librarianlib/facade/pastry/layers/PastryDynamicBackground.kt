package com.teamwizardry.librarianlib.facade.pastry.layers

import com.mojang.blaze3d.vertex.IVertexBuilder
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.pastry.IBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.Rect2dUnion
import com.teamwizardry.librarianlib.math.Direction2d
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.renderer.IRenderTypeBuffer
import java.awt.Color
import kotlin.math.abs
import kotlin.math.min

public class PastryDynamicBackground(style: IBackgroundStyle, vararg shapeLayers: GuiLayer): GuiLayer() {
    public constructor(vararg shapeLayers: GuiLayer): this(PastryBackgroundStyle.VANILLA, *shapeLayers)

    public var style: IBackgroundStyle = style
        set(value) {
            if(field != value) {
                field = value
                markLayoutDirty()
            }
        }

    public val shapeLayers: MutableList<GuiLayer> = mutableListOf(*shapeLayers)

    private val elements = mutableListOf<Element>()
    private var frames = listOf<Rect2d>()

    public fun addShapeLayers(vararg layers: GuiLayer) {
        shapeLayers.addAll(layers)
    }

    override fun prepareLayout() {
        val newFrames = shapeLayers
            .filter { it.root === this.root } // only related layers
            .map { it.convertRectTo(it.bounds, this) }

        if(newFrames != frames) {
            frames = newFrames
            updateElements()
        }
    }

    override fun draw(context: GuiDrawContext) {
        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(style.edges.renderType)
        for(element in elements) {
            element.draw(context.matrix, vb)
        }
        buffer.finish()
    }

    // yes this creates a lot of temporary Vec2d objects, but efficiency be damned this is hard enough as it is and
    // shouldn't be especially hot code
    private fun updateElements() {
        val edgeSize = style.edgeSize
        val edgeInset = style.edgeInset
        val edgeOutset = edgeSize - edgeInset

        elements.clear()

        for(frame in frames) {
            elements.add(Element(frame.pos, frame.size, 0, 2))
        }

        val union = Rect2dUnion(frames)
        union.compute()
        val segments = (union.horizontalSegments.asSequence() + union.verticalSegments.asSequence())
            .filterNotNull()
            .filter { it.depth == 0 }
            .toList()
        // used to compute what corner to use
        val startSet = segments.mapTo(mutableSetOf()) { it.startVec to it.direction }
        val endSet = segments.mapTo(mutableSetOf()) { it.endVec to it.direction }

        for(segment in segments) {
            val endIsInnerCorner = startSet.contains(segment.endVec to segment.direction.rotateCCW())
            val startIsInnerCorner = endSet.contains(segment.startVec to segment.direction.rotateCW())

            // the two opposite corners of the edge. The min/max of these two will be used for the final position/size
            var edgeStart = segment.startVec
            var edgeEnd = segment.endVec

            // shift the edge in
            edgeStart -= segment.side.vector * edgeInset
            edgeEnd -= segment.side.vector * edgeInset

            // get out of the way of the top of an inner corner or the bottom of an outer corner
            edgeStart += segment.direction.vector * if(startIsInnerCorner) edgeOutset else edgeInset
            edgeEnd -= segment.direction.vector * if(endIsInnerCorner) edgeOutset else edgeInset

            var cornerStart = edgeEnd
            var cornerEnd = edgeEnd + segment.direction.vector * edgeSize

            edgeEnd += segment.side.vector * edgeSize
            cornerEnd += segment.side.vector * edgeSize

            val (edgeU: Int, edgeV: Int) = when(segment.side) {
                Direction2d.UP -> 0 to 4
                Direction2d.DOWN -> 0 to 5
                Direction2d.LEFT -> 2 to 7
                Direction2d.RIGHT -> 3 to 7
            }

            val (cornerU: Int, cornerV: Int) = if(endIsInnerCorner) {
                when(segment.side) {
                    Direction2d.UP -> 3 to 5
                    Direction2d.DOWN -> 2 to 4
                    Direction2d.LEFT -> 3 to 4
                    Direction2d.RIGHT -> 2 to 5
                }
            } else {
                when(segment.side) {
                    Direction2d.UP -> 3 to 2
                    Direction2d.DOWN -> 2 to 3
                    Direction2d.LEFT -> 2 to 2
                    Direction2d.RIGHT -> 3 to 3
                }
            }

            elements.add(Element(
                vec(min(edgeStart.x, edgeEnd.x), min(edgeStart.y, edgeEnd.y)),
                vec(abs(edgeEnd.x - edgeStart.x), abs(edgeEnd.y - edgeStart.y)),
                edgeU, edgeV
            ))
            elements.add(Element(
                vec(min(cornerStart.x, cornerEnd.x), min(cornerStart.y, cornerEnd.y)),
                vec(edgeSize, edgeSize),
                cornerU, cornerV
            ))
        }
    }

    private inner class Element(
        val pos: Vec2d,
        val size: Vec2d,
        val tileU: Int, val tileV: Int
    ) {
        val minU: Float get() = style.edges.interpU(tileU / 4f)
        val maxU: Float get() = style.edges.interpU((tileU + 1) / 4f)
        val minV: Float get() = style.edges.interpV(tileV / 8f)
        val maxV: Float get() = style.edges.interpV((tileV + 1) / 8f)
        val minX: Double get() = pos.x
        val minY: Double get() = pos.y
        val maxX: Double get() = pos.x + size.x
        val maxY: Double get() = pos.y + size.y

        fun draw(matrix: Matrix3d, vb: IVertexBuilder) {
            val tint = Color.WHITE

            vb.pos2d(matrix, minX, maxY).color(tint).tex(minU, maxV).endVertex()
            vb.pos2d(matrix, maxX, maxY).color(tint).tex(maxU, maxV).endVertex()
            vb.pos2d(matrix, maxX, minY).color(tint).tex(maxU, minV).endVertex()
            vb.pos2d(matrix, minX, minY).color(tint).tex(minU, minV).endVertex()
        }
    }
}
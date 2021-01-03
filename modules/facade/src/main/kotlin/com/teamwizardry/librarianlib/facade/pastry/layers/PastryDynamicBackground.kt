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
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.renderer.IRenderTypeBuffer
import java.awt.Color
import kotlin.math.abs
import kotlin.math.min

public class PastryDynamicBackground(public var style: IBackgroundStyle): GuiLayer() {
    public constructor(): this(PastryBackgroundStyle.DEFAULT)

    private val elements = mutableListOf<Element>()

    override fun layoutChildren() {
        updateElements()
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
        val borderSize = style.edgeSize

        elements.clear()

        val frames = children.filter { it.isVisible }.map { it.frame }

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

            if(startIsInnerCorner) {
                edgeStart += segment.direction.direction * borderSize
            }
            if(endIsInnerCorner) {
                edgeEnd -= segment.direction.direction * borderSize
            }

            var cornerStart = edgeEnd
            var cornerEnd = edgeEnd + segment.direction.direction * borderSize

            edgeEnd += segment.side.direction * borderSize
            cornerEnd += segment.side.direction * borderSize

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

            val edgePos = vec(min(edgeStart.x, edgeEnd.x), min(edgeStart.y, edgeEnd.y))
            val edgeSize = vec(abs(edgeEnd.x - edgeStart.x), abs(edgeEnd.y - edgeStart.y))
            val cornerPos = vec(min(cornerStart.x, cornerEnd.x), min(cornerStart.y, cornerEnd.y))

            elements.add(Element(edgePos, edgeSize, edgeU, edgeV))
            elements.add(Element(cornerPos, vec(borderSize, borderSize), cornerU, cornerV))
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
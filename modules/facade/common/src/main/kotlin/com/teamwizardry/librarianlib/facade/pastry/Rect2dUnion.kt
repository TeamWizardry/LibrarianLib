package com.teamwizardry.librarianlib.facade.pastry

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Axis2d
import com.teamwizardry.librarianlib.math.Direction2d
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import kotlin.math.max
import kotlin.math.min

public class Rect2dUnion(rects: List<Rect2d>) {
    public var horizontalSegments: MutableList<Segment?> = mutableListOf()
    public var verticalSegments: MutableList<Segment?> = mutableListOf()
    private val rawHorizontalSegments: List<Segment?>
    private val rawVerticalSegments: List<Segment?>

    init {
        for(rect in rects) {
            horizontalSegments.add(Segment(rect.minY, rect.minX, rect.maxX, Direction2d.UP))
            horizontalSegments.add(Segment(rect.maxY, rect.minX, rect.maxX, Direction2d.DOWN))
            verticalSegments.add(Segment(rect.minX, rect.minY, rect.maxY, Direction2d.LEFT))
            verticalSegments.add(Segment(rect.maxX, rect.minY, rect.maxY, Direction2d.RIGHT))
        }
        horizontalSegments.sortBy { it?.position }
        verticalSegments.sortBy { it?.position }
        rawHorizontalSegments = horizontalSegments.map { it?.copy() }
        rawVerticalSegments = verticalSegments.map { it?.copy() }
    }

    public fun compute() {
        mergeCollinear()
        computeDepths()
    }

    @JvmSynthetic
    internal fun mergeCollinear() {
        mergeCollinear(verticalSegments)
        mergeCollinear(horizontalSegments)
    }

    private fun mergeCollinear(segments: MutableList<Segment?>) {
        for(mainSegment in segments) {
            mainSegment ?: continue

            segments.forEachIndexed { i, segment ->
                segment ?: return@forEachIndexed
                if(segment === mainSegment) return@forEachIndexed

                if(
                    mainSegment.isMinEdge == segment.isMinEdge &&
                    mainSegment.position == segment.position &&
                    mainSegment.min <= segment.max && segment.min <= mainSegment.max
                ) {
                    mainSegment.min = min(mainSegment.min, segment.min)
                    mainSegment.max = max(mainSegment.max, segment.max)
                    segments[i] = null
                }
            }
        }
    }

    @JvmSynthetic
    internal fun computeDepths() {
        horizontalSegments = computeDepths(horizontalSegments, rawVerticalSegments)
        verticalSegments = computeDepths(verticalSegments, rawHorizontalSegments)
    }

    private fun computeDepths(segments: MutableList<Segment?>, crossingSegments: List<Segment?>): MutableList<Segment?> {
        val newSegments = mutableListOf<Segment?>()
        for(segment in segments) {
            segment ?: continue

            var min = segment.min
            var depth = 0
            crossingChecks@for(crossing in crossingSegments) {
                crossing ?: continue
                // this even counts crossings before or after the line segment
                val crossesAnywhere = if(segment.isMinEdge)
                    segment.position > crossing.min && segment.position <= crossing.max
                else
                    segment.position >= crossing.min && segment.position < crossing.max
                if(!crossesAnywhere) continue

                // we're past the end of the segment, no sense continuing to calculate depths
                if(crossing.position > segment.max) break@crossingChecks

                // we're inside the line segment and wouldn't create a zero-length segment
                if(crossing.position > min) {
                    newSegments.add(segment.copy(min = min, max = crossing.position, depth = depth))
                    min = crossing.position
                }

                // increment or decrement the depth as appropriate
                if(crossing.isMinEdge)
                    depth++
                else
                    depth--
            }
            if(min < segment.max)
                newSegments.add(segment.copy(min = min, max = segment.max, depth = depth))
        }
        return newSegments
    }

    public data class Segment(
        /**
         * The crosswise position of the segment. The X position for vertical segments and the Y position for horizontal segments
         */
        var position: Double,
        var min: Double,
        var max: Double,
        val side: Direction2d,
        var depth: Int = 0
    ) {
        val direction: Direction2d
            get() = side.rotateCW()
        val minVec: Vec2d
            get() = if(side.axis == Axis2d.X) vec(position, min) else vec(min, position)
        val maxVec: Vec2d
            get() = if(side.axis == Axis2d.X) vec(position, max) else vec(max, position)
        val startVec: Vec2d
            get() = if(side.rotation < 2) minVec else maxVec
        val endVec: Vec2d
            get() = if(side.rotation < 2) maxVec else minVec

        val isMinEdge: Boolean
            get() = side.sign == -1
    }
}
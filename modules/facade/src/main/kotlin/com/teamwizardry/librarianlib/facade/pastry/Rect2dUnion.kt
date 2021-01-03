package com.teamwizardry.librarianlib.facade.pastry

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import kotlin.math.max
import kotlin.math.min

/**
 *
 */
public class Rect2dUnion(rects: List<Rect2d>) {
    private val rects = rects.toList()
    public var horizontalSegments: MutableList<Segment?> = mutableListOf()
    public var verticalSegments: MutableList<Segment?> = mutableListOf()
    public val rawHorizontalSegments: List<Segment?>
    public val rawVerticalSegments: List<Segment?>

    init {
        for(rect in rects) {
            horizontalSegments.add(Segment(rect.minY, rect.minX, rect.maxX, false, false))
            horizontalSegments.add(Segment(rect.maxY, rect.minX, rect.maxX, true, false))
            verticalSegments.add(Segment(rect.maxX, rect.minY, rect.maxY, false, true))
            verticalSegments.add(Segment(rect.minX, rect.minY, rect.maxY, true, true))
        }
        horizontalSegments.sortBy { it?.position }
        verticalSegments.sortBy { it?.position }
        rawHorizontalSegments = horizontalSegments.map { it?.copy() }
        rawVerticalSegments = verticalSegments.map { it?.copy() }
    }

    public fun mergeCollinear() {
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
                    mainSegment.negative == segment.negative &&
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

    public fun computeDepths() {
        for(segment in horizontalSegments) {
            segment ?: continue
            val minVec = segment.minVec

            // don't include the minX edge, because we'll immediately do a depth++ when we hit that edge
            segment.depth = rects.count {
                minVec.x > it.minX && minVec.y >= it.minY && minVec.x <= it.maxX && minVec.y <= it.maxY
            }
        }
        for(segment in verticalSegments) {
            segment ?: continue
            val minVec = segment.minVec

            // don't include the minY edge, because we'll immediately do a depth++ when we hit that edge
            segment.depth = rects.count {
                minVec.x >= it.minX && minVec.y > it.minY && minVec.x <= it.maxX && minVec.y <= it.maxY
            }
        }

        horizontalSegments = computeDepths(horizontalSegments, rawVerticalSegments)
        verticalSegments = computeDepths(verticalSegments, rawHorizontalSegments)
    }

    public fun computeDepths(segments: MutableList<Segment?>, crossingSegments: List<Segment?>): MutableList<Segment?> {
        val newSegments = mutableListOf<Segment?>()
        for(segment in segments) {
            segment ?: continue

            var min = segment.min
            var depth = segment.depth
            for(crossing in crossingSegments) {
                crossing ?: continue
                if(crossing.position < segment.min || crossing.position > segment.max) continue

                if(
//                    segment.position >= crossing.min && segment.position <= crossing.max
                    (segment.isEnteringEdge && segment.position > crossing.min && segment.position <= crossing.max) ||
                    (!segment.isEnteringEdge && segment.position >= crossing.min && segment.position < crossing.max)
                ) {
                    newSegments.add(segment.copy(min = min, max = crossing.position, depth = depth))
                    min = crossing.position
                    if(crossing.isEnteringEdge)
                        depth++
                    else
                        depth--
                }
            }
            newSegments.add(segment.copy(min = min, max = segment.max, depth = depth))
        }
        newSegments.removeIf { it?.min == it?.max }
        return newSegments
    }

    public data class Segment(
        /**
         * The crosswise position of the segment. The X position for vertical segments and the Y position for horizontal segments
         */
        var position: Double,
        var min: Double,
        var max: Double,
        val negative: Boolean,
        val vertical: Boolean,
        var depth: Int = 0
    ) {

        /**
         * The lengthwise start of this segment. e.g. the Y position for vertical segments and the X position for horizontal segments
         */
        val start: Double
            get() = if(negative) max else min
        /**
         * The length of this segment. Negative for segments pointing in the negative direction
         */
        val length: Double
            get() = if(negative) min - max else max - min

        val minVec: Vec2d
            get() = if(vertical) vec(position, min) else vec(min, position)
        val maxVec: Vec2d
            get() = if(vertical) vec(position, max) else vec(max, position)
        val startVec: Vec2d
            get() = if(vertical) vec(position, start) else vec(start, position)
        val lengthVec: Vec2d
            get() = if(vertical) vec(0, length) else vec(length, 0)

        /**
         * Whether approaching this edge from the negative direction represents entering a rect
         */
        val isEnteringEdge: Boolean
            get() = if(vertical) negative else !negative
    }
}
package com.teamwizardry.librarianlib.facade.layers.text

import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.text.BitfontRenderer
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.core.util.rect
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Vec2d
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import java.awt.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

public class BitfontLayer(posX: Int, posY: Int, width: Int, height: Int) : GuiLayer(posX, posY, width, height) {
    public val container: TextContainer = TextContainer(1)

    /**
     * The color of the text. (can be overridden by color attributes in the string)
     */
    public val color_im: IMValue<Color> = imValue(Color.BLACK)

    /**
     * The color of the text. (can be overridden by color attributes in the string)
     */
    public var color: Color by color_im

    /**
     * If and how this layer should automatically fit its size to the contained text.
     */
    public var textFitting: TextFit = TextFit.NONE

    /**
     * The logical bounds of the text
     */
    public var textBounds: Rect2d = rect(0, 0, 0, 0)
        private set

    public data class CursorPosition(
        /**
         * The index of the grapheme cluster.
         */
        val index: Int,
        /**
         * The position of the character. [ascent] and [descent] are relative to this, but this is not necessarily
         * between them (e.g. when the cursor is on a combining character beyond the line bounds)
         */
        val pos: Vec2d,
        /**
         * The ascent of the cursor above the position
         */
        val ascent: Double,
        /**
         * The descent of the cursor below the position
         */
        val descent: Double,
        /**
         * The line number of the cursor
         */
        val line: Int,
        /**
         * The column of the glyph. Specifically the number of grapheme clusters before the cursor.
         */
        val column: Int,

        val outOfBoundsType: CursorOutOfBoundsType
    ) {
        public constructor(
            clusterStart: Int,
            lineMetrics: TextContainer.TypesetLine,
            baselinePos: Int,
            line: Int,
            column: Int,
            outOfBoundsType: CursorOutOfBoundsType
        ) : this(
            clusterStart,
            vec(
                lineMetrics.posX + baselinePos,
                lineMetrics.posY + lineMetrics.baseline
            ),
            lineMetrics.baseline.toDouble(), lineMetrics.height.toDouble() - lineMetrics.baseline,
            line, column,
            outOfBoundsType
        )
    }

    public enum class CursorOutOfBoundsType {
        IN_BOUNDS,
        INDEX_BEFORE_START,
        INDEX_AFTER_END,
        POSITION_BEFORE_START,
        POSITION_AFTER_END,
    }

    public sealed class CursorQuery {
        public abstract fun apply(container: TextContainer): CursorPosition?

        public data class ByIndex(
            /**
             * The index of the character in the string
             */
            val index: Int,
        ) : CursorQuery() {
            override fun apply(container: TextContainer): CursorPosition? {
                if(container.lines.isEmpty())
                    return null

                if(index < container.startIndex) {
                    val line = container.lines.first()
                    return CursorPosition(
                        container.startIndex,
                        line, line.baselineStart,
                        0, 0,
                        CursorOutOfBoundsType.INDEX_BEFORE_START
                    )
                }
                if(index >= container.endIndex) {
                    val line = container.lines.last()
                    return CursorPosition(
                        container.endIndex,
                        line, line.baselineEnd,
                        container.lines.lastIndex, line.clusters.size,
                        CursorOutOfBoundsType.INDEX_AFTER_END
                    )
                }

                for((row, line) in container.lines.withIndex()) {
                    for((col, cluster) in line.clusters.withIndex()) {
                        if(index in cluster.index until cluster.afterIndex) {
                            return CursorPosition(
                                container.endIndex,
                                line, cluster.baselineStart,
                                row, col,
                                CursorOutOfBoundsType.IN_BOUNDS
                            )
                        }
                    }
                }

                return null
            }
        }

        public data class ByPosition(
            /**
             * The position to search
             */
            val pos: Vec2d,
            /**
             * Query a specific line, ignoring the Y coordinate
             */
            val specificLine: Int? = null
        ) : CursorQuery() {

            override fun apply(container: TextContainer): CursorPosition? {
                val closestRow: Int
                val closestLine: TextContainer.TypesetLine
                if(specificLine != null) {
                    closestRow = specificLine
                    closestLine = container.lines.getOrNull(specificLine) ?: return null
                } else {
                    if(container.lines.isEmpty())
                        return null

                    val first = container.lines.first()
                    if(pos.y < first.posY - first.height/2) {
                        return CursorPosition(
                            container.startIndex,
                            first, first.baselineStart,
                            0, 0,
                            CursorOutOfBoundsType.POSITION_BEFORE_START
                        )
                    }
                    val last = container.lines.last()
                    if(pos.y > last.posY + last.height + last.height/2) {
                        return CursorPosition(
                            container.endIndex,
                            last, last.baselineEnd,
                            container.lines.lastIndex, last.clusters.size,
                            CursorOutOfBoundsType.POSITION_AFTER_END
                        )
                    }

                    container.lines.withIndex().minByOrNull { (_, line) ->
                        min(abs(pos.y - line.posY), abs(pos.y - (line.posY + line.height)))
                    }!!.also { // non-null because lines will never be empty (we check at the start)
                        closestRow = it.index
                        closestLine = it.value
                    }
                }

                if(closestLine.clusters.isEmpty())
                    return null

                if(pos.x < closestLine.posX + closestLine.baselineStart) {
                    return CursorPosition(
                        closestLine.startIndex,
                        closestLine, closestLine.baselineStart,
                        closestRow, 0,
                        CursorOutOfBoundsType.POSITION_BEFORE_START
                    )
                }
                if(pos.x > closestLine.posX + closestLine.baselineEnd) {
                    return CursorPosition(
                        closestLine.endIndexNoNewline,
                        closestLine, closestLine.baselineEnd,
                        closestRow, closestLine.clusters.size,
                        CursorOutOfBoundsType.POSITION_AFTER_END
                    )
                }

                var closestDistance = Double.POSITIVE_INFINITY
                var closestColumn = 0
                var closestIndex = 0
                var closestBaseline = 0

                val lineX = pos.x - closestLine.posX
                for((column, cluster) in closestLine.clusters.withIndex()) {
                    val startDistance = abs(cluster.baselineStart - lineX)
                    if(startDistance < closestDistance) {
                        closestDistance = startDistance
                        closestColumn = column
                        closestIndex = cluster.index
                        closestBaseline = cluster.baselineStart
                    }
                    val endDistance = abs(cluster.baselineEnd - lineX)
                    if(endDistance < closestDistance) {
                        closestDistance = endDistance
                        closestColumn = column + 1
                        closestIndex = cluster.afterIndex
                        closestBaseline = cluster.baselineEnd
                    }
                }

                return CursorPosition(
                    closestIndex,
                    closestLine, closestBaseline,
                    closestRow, closestColumn,
                    CursorOutOfBoundsType.IN_BOUNDS
                )
            }
        }
    }

    public fun prepareTextContainer() {
        container.width = this.widthi
        container.height = this.heighti

        when (textFitting) {
            TextFit.NONE -> {
            }
            TextFit.VERTICAL_SHRINK, TextFit.VERTICAL -> {
                container.height = Int.MAX_VALUE
            }
            TextFit.HORIZONTAL -> {
                container.width = Int.MAX_VALUE
            }
            TextFit.BOTH -> {
                container.height = Int.MAX_VALUE
                container.width = Int.MAX_VALUE
            }
        }
    }

    public fun applyTextLayout() {
        var minX = 0
        var minY = 0
        var maxX = 0
        var maxY = 0
        container.lines.forEach { line ->
            minX = min(minX, line.posX)
            minY = min(minY, line.posY)

            maxX = max(maxX, line.posX + (line.clusters.maxOfOrNull { if(it.isBlank) 0 else it.baselineEnd } ?: 0))
            maxY = max(maxY, line.posY + line.height)
        }
        textBounds = rect(minX, minY, maxX, maxY)

        when (textFitting) {
            TextFit.NONE -> {
            }
            TextFit.VERTICAL -> {
                heighti = maxY
            }
            TextFit.HORIZONTAL -> {
                widthi = maxX
            }
            TextFit.VERTICAL_SHRINK, TextFit.BOTH -> {
                heighti = maxY
                widthi = maxX
            }
        }

        container.width = this.widthi
        container.height = this.heighti
    }

    override fun draw(context: GuiDrawContext) {
        BitfontRenderer.draw(context.transform, container, color)
    }
}

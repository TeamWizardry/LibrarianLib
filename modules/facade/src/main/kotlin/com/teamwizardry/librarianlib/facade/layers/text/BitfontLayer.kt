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
import dev.thecodewarrior.bitfont.typesetting.TypesetGlyph
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
         * The index of the grapheme cluster. Note that this will be inaccurate when the cursor is placed beyond the
         * end of the test range
         */
        val clusterStart: Int,
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
    )

    public enum class CursorOutOfBoundsType {
        IN_BOUNDS,
        INDEX_BEFORE_START,
        INDEX_AFTER_END,
        POSITION_BEFORE_START,
        POSITION_AFTER_END,
    }

    public sealed class CursorQuery {
        /**
         * Use the line's Y position and height, instead of the individual glyph's Y position and height
         */
        public abstract val useLineVMetrics: Boolean

        public abstract fun apply(container: TextContainer): CursorPosition?

        protected fun cursor(
            line: TextContainer.TypesetLine,
            glyph: TypesetGlyph,
            useLineVMetrics: Boolean,
            row: Int,
            column: Int,
            after: Boolean = false,
            bounds: CursorOutOfBoundsType = CursorOutOfBoundsType.IN_BOUNDS
        ): CursorPosition {
            val pos = vec(
                line.posX + if(after) glyph.afterX else glyph.posX,
                line.posY + glyph.posY
            )
            return CursorPosition(
                glyph.characterIndex,
                pos,
                (if (useLineVMetrics) glyph.posY else glyph.ascent).toDouble(),
                (if (useLineVMetrics) line.height - glyph.posY else glyph.descent).toDouble(),
                row, column,
                bounds
            )
        }

        public data class ByIndex(
            /**
             * The index of the character in the string
             */
            val index: Int,
            /**
             * Use the line's Y position and height, instead of the individual glyph's Y position and height
             */
            override val useLineVMetrics: Boolean,
        ) : CursorQuery() {
            override fun apply(container: TextContainer): CursorPosition? {
                var min: CursorPosition? = null
                var minIndex = Int.MAX_VALUE
                var max: CursorPosition? = null
                var maxIndex = Int.MIN_VALUE

                for ((row, line) in container.lines.withIndex()) {
                    for ((column, cluster) in line.clusters.withIndex()) {
                        if(cluster.main.characterIndex < minIndex) {
                            minIndex = cluster.main.characterIndex
                            min = cursor(
                                line,
                                cluster.main,
                                useLineVMetrics,
                                row,
                                column,
                                bounds = CursorOutOfBoundsType.INDEX_BEFORE_START
                            )
                        }
                        if(cluster.main.characterIndex > maxIndex) {
                            maxIndex = cluster.main.characterIndex
                            max = cursor(
                                line,
                                cluster.main,
                                useLineVMetrics,
                                row,
                                column,
                                after = true,
                                bounds = CursorOutOfBoundsType.INDEX_AFTER_END
                            )
                        }
                        if (cluster.main.characterIndex == index || cluster.attachments.any { it.characterIndex == index }) {
                            return cursor(
                                line,
                                cluster.main,
                                useLineVMetrics,
                                row,
                                column
                            )
                        }
                    }
                }
                if(index < minIndex)
                    return min
                else
                    return max
            }
        }

        public data class ByPosition(
            /**
             * The position to search
             */
            val pos: Vec2d,
            /**
             * Use the line's Y position and height, instead of the individual glyph's Y position and height
             */
            override val useLineVMetrics: Boolean,
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

                    container.lines.first().also { line ->
                        if(pos.y < line.posY - line.height/2) {
                            val min = line.clusters.withIndex().minByOrNull { (_, cluster) -> cluster.main.characterIndex }
                                ?: return@apply null
                            return@apply cursor(
                                line,
                                min.value.main,
                                useLineVMetrics,
                                specificLine ?: 0,
                                min.index,
                                bounds = CursorOutOfBoundsType.POSITION_BEFORE_START
                            )
                        }
                    }
                    container.lines.last().also { line ->
                        if(pos.y > line.posY + line.height + line.height/2) {
                            val max = line.clusters.withIndex().maxByOrNull { (_, cluster) -> cluster.main.characterIndex }
                                ?: return@apply null
                            return@apply cursor(
                                line,
                                max.value.main,
                                useLineVMetrics,
                                specificLine ?: container.lines.lastIndex,
                                max.index,
                                after = true,
                                bounds = CursorOutOfBoundsType.POSITION_AFTER_END
                            )
                        }
                    }

                    val closest = container.lines.withIndex().minByOrNull { (_, line) ->
                        min(abs(pos.y - line.posY), abs(pos.y - (line.posY + line.height)))
                    }!!.also { // non-null because lines will never be empty (we check at the start)
                        closestRow = it.index
                        closestLine = it.value
                    }
                }

                if(closestLine.clusters.isEmpty())
                    return null

                closestLine.also { line ->
                    val min = line.clusters.withIndex().minByOrNull { (_, cluster) -> cluster.main.posX }!!
                    if(pos.x < line.posX + min.value.main.posX) {
                        return@apply cursor(
                            line,
                            min.value.main,
                            useLineVMetrics,
                            closestRow,
                            min.index,
                            bounds = CursorOutOfBoundsType.POSITION_BEFORE_START
                        )
                    }
                }
                closestLine.also { line ->
                    val max = line.clusters.withIndex().maxByOrNull { (_, cluster) -> cluster.main.posX }!!
                    if(pos.x > line.posX + max.value.main.afterX) {
                        return@apply cursor(
                            line,
                            max.value.main,
                            useLineVMetrics,
                            closestRow,
                            max.index,
                            after = true,
                            bounds = CursorOutOfBoundsType.POSITION_AFTER_END
                        )
                    }
                }

                val (closestColumn, closestCluster) = closestLine.clusters.withIndex().minByOrNull { (_, cluster) ->
                    abs(pos.x - (closestLine.posX + cluster.main.posX))
                }!!

                return cursor(
                    closestLine,
                    closestCluster.main,
                    useLineVMetrics,
                    closestRow,
                    closestColumn,
                    after = true,
                    bounds = CursorOutOfBoundsType.IN_BOUNDS
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

            maxX = max(maxX, line.posX + (line.clusters.lastOrNull() { !it.isInvisible }?.main?.afterX ?: 0))
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

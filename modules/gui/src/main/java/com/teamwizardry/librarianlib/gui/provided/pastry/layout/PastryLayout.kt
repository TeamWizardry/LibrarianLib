package com.teamwizardry.librarianlib.gui.provided.pastry.layout

import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.ceilInt
import kotlin.math.max
import kotlin.math.roundToInt

class PastryLayout: GuiComponent() {
    val rows: List<Row>
        get() = children.filterIsInstance<Row>()

    fun addRow(): RowBuilder {
        val row = Row()
        this.add(row)
        return RowBuilder(row)
    }

    fun addPadding(cells: Int) {
        this.add(PaddingLayer(0, cells))
    }

    override fun layoutChildren() {
//        var cellWidth = 0
//        forEachChild { child ->
//            if(child.hasTag(EXCLUDED_TAG))
//                return@forEachChild
//            val span = if(child is PaddingLayer)
//                child.cellWidth
//            else
//                span(child.width)
//            cellWidth = max(cellWidth, span)
//        }
//        this.widthi = cellWidth * TOTAL_CELL_SIZE

        children.forEach { it.runLayoutIfNeeded() }
        var cell = 0
        forEachChild { child ->
            if(child.hasTag(EXCLUDED_TAG))
                return@forEachChild
            val span = if(child is PaddingLayer)
                child.cellHeight * TOTAL_CELL_HEIGHT
            else
                child.heighti //ceilInt((child.height + CELL_PADDING * 2) / TOTAL_CELL_HEIGHT)

//            child.pos = vec(0, cell * TOTAL_CELL_HEIGHT + CELL_PADDING)
            child.pos = vec(0, cell + CELL_PADDING)
            cell += span + CELL_PADDING * 2
            if(child is Row)
                child.width = this.width
        }
    }

    class RowBuilder(val row: Row) {
        fun addPadding(horizontal: Int): RowBuilder {
            this.addPadding(horizontal, 0)
            return this
        }

        fun addPadding(horizontal: Int, vertical: Int): RowBuilder {
            this.add(PaddingLayer(horizontal, vertical))
            return this
        }

        fun add(vararg layers: GuiLayer): RowBuilder {
            row.add(*layers)
            return this
        }
    }

    class Row: GuiComponent() {
        override fun layoutChildren() {
            children.forEach { it.runLayoutIfNeeded() }

            var cellHeight = 0
            forEachChild { child ->
                if(child.hasTag(EXCLUDED_TAG))
                    return@forEachChild
                val span = if(child is PaddingLayer)
                    child.cellHeight
                else
                    ceilInt((child.height + CELL_PADDING * 2) / TOTAL_CELL_HEIGHT)
                cellHeight = max(cellHeight, span)
            }
            this.heighti = cellHeight * CELL_HEIGHT

            var cell = 0
            forEachChild { child ->
                if(child.hasTag(EXCLUDED_TAG))
                    return@forEachChild
                val span = if(child is PaddingLayer)
                    child.cellWidth
                else
                    ceilInt((child.width + CELL_PADDING * 2) / TOTAL_CELL_WIDTH)

                val newX = cell * TOTAL_CELL_WIDTH + when {
                    child.hasTag(ALIGN_RIGHT) -> (span * TOTAL_CELL_WIDTH - child.width) - CELL_PADDING
                    child.hasTag(ALIGN_CENTER) -> (span * TOTAL_CELL_WIDTH - child.width) / 2
                    else -> CELL_PADDING.toDouble()
                }

                child.pos = vec(
                    newX,
                    ((height - child.height) / 2).roundToInt()
                )
                cell += span
            }
        }
    }

    private class PaddingLayer(val cellWidth: Int, val cellHeight: Int): GuiLayer()

    companion object {
        @JvmField
        val CELL_WIDTH: Int = 8
        @JvmField
        val CELL_HEIGHT: Int = 12
        @JvmField
        val CELL_PADDING: Int = 1
        @JvmField
        val TOTAL_CELL_HEIGHT: Int = CELL_HEIGHT + CELL_PADDING * 2
        @JvmField
        val TOTAL_CELL_WIDTH: Int = CELL_WIDTH + CELL_PADDING * 2

        /**
         * Layers with this tag will be ignored when laying out children
         */
        @JvmField
        val EXCLUDED_TAG: Any = Any()

        @JvmField
        val ALIGN_CENTER: Any = Any()

        @JvmField
        val ALIGN_RIGHT: Any = Any()
    }
}

package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.RMValueDouble
import com.teamwizardry.librarianlib.facade.value.RMValueInt
import com.teamwizardry.librarianlib.math.ceilInt
import com.teamwizardry.librarianlib.math.vec

public class SlotGridLayer(
    posX: Int,
    posY: Int,
    public val region: SlotRegion,
    columns: Int,
    padding: Int
): GuiLayer(posX, posY) {
    public val columns_rm: RMValueInt = rmInt(columns) { _, _ -> markLayoutDirty() }
    public var columns: Int by columns_rm
    public val padding_rm: RMValueInt = rmInt(padding) { _, _ -> markLayoutDirty() }
    public var padding: Int by padding_rm

    private val slots = region.map { SlotLayer(it, 0, 0) }

    init {
        slots.forEach {
            add(it)
        }
        val rows = ceilInt(slots.size / columns.toDouble())
        this.size = vec(columns * 16 + (columns - 1) * padding, rows * 16 + (rows - 1) * padding)
    }

    override fun layoutChildren() {
        val padding = padding
        val columns = columns
        val rows = ceilInt(slots.size / columns.toDouble())
        this.size = vec(columns * 16 + (columns - 1) * padding, rows * 16 + (rows - 1) * padding)
        slots.forEachIndexed { i, slot ->
            val column = i % columns
            val row = i / columns
            slot.pos = vec(column * (16 + padding), row * (16 + padding))
        }
    }
}
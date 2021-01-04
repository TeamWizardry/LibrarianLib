package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.RMValueDouble
import com.teamwizardry.librarianlib.facade.value.RMValueInt
import com.teamwizardry.librarianlib.math.ceilInt
import com.teamwizardry.librarianlib.core.util.vec

public class SlotGridLayer @JvmOverloads constructor(
    posX: Int,
    posY: Int,
    public val region: SlotRegion,
    columns: Int,
    showBackground: Boolean = true,
    padding: Int = 0
): GuiLayer(posX, posY) {
    public val columns_rm: RMValueInt = rmInt(columns) { _, _ -> markLayoutDirty() }
    public var columns: Int by columns_rm
    public val padding_rm: RMValueInt = rmInt(padding) { _, _ -> markLayoutDirty() }
    public var padding: Int by padding_rm

    // the margin around all sides of a slot, distinct from the padding, which is only *between* slots
    private var slotMargin: Int = if(showBackground) 1 else 0

    private val slots = region.map {
        SlotLayer(it, 0, 0, showBackground)
    }

    init {
        slots.forEach {
            add(it)
        }
        val rows = ceilInt(slots.size / columns.toDouble())
        this.size = vec(
            columns * 16 + (columns - 1) * padding + columns * 2 * slotMargin,
            rows * 16 + (rows - 1) * padding + rows * 2 * slotMargin
        )
    }

    override fun layoutChildren() {
        val rows = ceilInt(slots.size / columns.toDouble())
        this.size = vec(
            columns * 16 + (columns - 1) * padding + columns * 2 * slotMargin,
            rows * 16 + (rows - 1) * padding + rows * 2 * slotMargin
        )
        slots.forEachIndexed { i, slot ->
            val column = i % columns
            val row = i / columns
            slot.pos = vec(
                slotMargin + column * (16 + padding) + column * 2 * slotMargin,
                slotMargin + row * (16 + padding) + row * 2 * slotMargin
            )
        }
    }
}
package com.teamwizardry.librarianlib.common.container

import com.teamwizardry.librarianlib.common.container.builtin.BaseWrappers
import com.teamwizardry.librarianlib.common.container.internal.SlotBase
import net.minecraftforge.items.IItemHandler

/**
 * Used to provide a convenient slot meaning association. See [BaseWrappers] for examples.
 */
open class InventoryWrapper(val inventory: IItemHandler) {
    val slotArray = (0..inventory.slots - 1).map { SlotBase(inventory, it) }
    val slots = SlotManager(slotArray)
    val types = TypeManager(slotArray)

    inner class SlotManager(val slotArray: List<SlotBase>) {

        operator fun get(range: IntRange): List<SlotBase> {
            return slotArray.subList(range.start, range.endInclusive + 1)
        }

        operator fun get(index: Int): SlotBase {
            return slotArray[index]
        }
    }

    inner class TypeManager(val slotArray: List<SlotBase>) {
        operator fun get(index: Int): SlotType {
            return slotArray[index].type
        }

        operator fun set(index: Int, type: SlotType) {
            slotArray[index].type = type
        }

        operator fun set(index: IntRange, type: SlotType) {
            for (i in index) {
                this[i] = type
            }
        }
    }
}

package com.teamwizardry.librarianlib.facade.container.slot

import com.teamwizardry.librarianlib.core.util.mapSrgName
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.inventory.container.Slot
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

public open class FacadeSlot(itemHandler: IItemHandler, index: Int) : SlotItemHandler(itemHandler, index, 0, 0) {

    public fun setX(xPos: Int) {
        setSlotX(this, xPos)
    }

    public fun setY(yPos: Int) {
        setSlotY(this, yPos)
    }

    // Fix MinecraftForge#7581
    override fun isSameInventory(other: Slot): Boolean {
        return other is SlotItemHandler && other.itemHandler == this.itemHandler
    }

    public companion object {
        @JvmStatic
        public fun setSlotX(slot: Slot, xPos: Int) {
            xPosMirror.setFast(slot, xPos)
        }

        @JvmStatic
        public fun setSlotY(slot: Slot, yPos: Int) {
            yPosMirror.setFast(slot, yPos)
        }

        private val xPosMirror = Mirror.reflectClass<Slot>().getDeclaredField(mapSrgName("field_75223_e"))
        private val yPosMirror = Mirror.reflectClass<Slot>().getDeclaredField(mapSrgName("field_75221_f"))
    }
}
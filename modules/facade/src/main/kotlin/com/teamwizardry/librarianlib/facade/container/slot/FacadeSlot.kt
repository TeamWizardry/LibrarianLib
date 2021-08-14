package com.teamwizardry.librarianlib.facade.container.slot

import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.facade.mixin.SlotPosAccess
import net.minecraft.inventory.Inventory
import net.minecraft.screen.slot.Slot

public open class FacadeSlot(itemHandler: Inventory, index: Int) : Slot(itemHandler, index, 0, 0) {

    public fun setX(xPos: Int) {
        mixinCast<SlotPosAccess>(this).slotX = xPos
    }

    public fun setY(yPos: Int) {
        mixinCast<SlotPosAccess>(this).slotY = yPos
    }

    public companion object {
        @JvmStatic
        public fun setSlotX(slot: Slot, xPos: Int) {
            mixinCast<SlotPosAccess>(slot).slotX = xPos
        }

        @JvmStatic
        public fun setSlotY(slot: Slot, yPos: Int) {
            mixinCast<SlotPosAccess>(slot).slotY = yPos
        }
    }
}
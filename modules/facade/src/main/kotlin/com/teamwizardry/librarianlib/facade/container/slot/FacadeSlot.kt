package com.teamwizardry.librarianlib.facade.container.slot

import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.facade.mixin.SlotPosAccess
import net.minecraft.inventory.Inventory
import net.minecraft.screen.slot.Slot

public open class FacadeSlot(itemHandler: Inventory, protected val index: Int) : Slot(itemHandler, index, 0, 0) {

    public fun setX(xPos: Int) {
        mixinCast<SlotPosAccess>(this).x = xPos
    }

    public fun setY(yPos: Int) {
        mixinCast<SlotPosAccess>(this).y = yPos
    }

    public companion object {
        @JvmStatic
        public fun setSlotX(slot: Slot, xPos: Int) {
            mixinCast<SlotPosAccess>(slot).x = xPos
        }

        @JvmStatic
        public fun setSlotY(slot: Slot, yPos: Int) {
            mixinCast<SlotPosAccess>(slot).y = yPos
        }
    }
}
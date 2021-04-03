package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.prism.Save
import com.teamwizardry.librarianlib.testcore.objects.TestTileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class SimpleInventoryTile(tileEntityTypeIn: TileEntityType<*>): TestTileEntity(tileEntityTypeIn) {
    @Save
    val inventory: ItemStackHandler = ItemStackHandler(16)
    private val inventoryCap: LazyOptional<IItemHandler> = LazyOptional.of { inventory }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == Direction.DOWN)
            return inventoryCap.cast()
        return super.getCapability(cap, side)
    }
}

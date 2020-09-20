package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.prism.Save
import com.teamwizardry.librarianlib.testbase.objects.TestTileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

class SimpleInventoryTile(tileEntityTypeIn: TileEntityType<*>): TestTileEntity(tileEntityTypeIn) {
    @Save
    val inventory: ItemStackHandler = ItemStackHandler(16)
}

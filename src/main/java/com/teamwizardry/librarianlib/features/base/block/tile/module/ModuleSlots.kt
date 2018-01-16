package com.teamwizardry.librarianlib.features.base.block.tile.module

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.RangedWrapper

/**
 * @author WireSegal
 * Created at 9:35 AM on 1/16/18.
 */
class ModuleSlots(handler: IItemHandlerModifiable, minSlot: Int, maxSlotExclusive: Int) : ITileModule {

    val wrapper = RangedWrapper(handler, minSlot, maxSlotExclusive)

    fun disallowSides(vararg sides: EnumFacing?) = apply { allowedSides.removeAll { it in sides } }
    fun setSides(vararg sides: EnumFacing?) = apply { allowedSides.clear(); allowedSides.addAll(sides) }

    private val allowedSides = mutableSetOf(*EnumFacing.VALUES, null)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T?
            = if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing in allowedSides) wrapper as T else null

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?)
            = capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing in allowedSides
}

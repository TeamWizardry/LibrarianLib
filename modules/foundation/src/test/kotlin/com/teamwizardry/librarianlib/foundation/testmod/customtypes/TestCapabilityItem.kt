package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.core.util.kotlin.getOrNull
import com.teamwizardry.librarianlib.foundation.item.BaseItem
import net.minecraft.item.ItemUseContext
import net.minecraft.util.ActionResultType
import net.minecraft.util.text.StringTextComponent

class TestCapabilityItem(properties: Properties): BaseItem(properties) {
    override fun onItemUse(context: ItemUseContext): ActionResultType {
        val te = context.world.getTileEntity(context.pos)
        val cap = te?.getCapability(TestCapability.capability)?.getOrNull()
        if(cap == null) {
            context.player?.sendMessage(StringTextComponent("No capability. Try clicking the test Tile Entity block."))
            return ActionResultType.FAIL
        }
        context.player?.sendMessage(StringTextComponent("Capability present! Data is ${cap.data}++"))
        cap.data++
        te.markDirty()

        return ActionResultType.SUCCESS
    }
}
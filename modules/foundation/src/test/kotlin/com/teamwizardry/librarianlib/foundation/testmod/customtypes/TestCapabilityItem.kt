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
            context.player?.sendStatusMessage(StringTextComponent("No capability. Try clicking the test Tile Entity block."), false)
            return ActionResultType.FAIL
        }
        context.player?.sendStatusMessage(StringTextComponent("Capability present! Data is ${cap.data}++"), false)
        cap.data++
        te.markDirty()

        return ActionResultType.SUCCESS
    }
}
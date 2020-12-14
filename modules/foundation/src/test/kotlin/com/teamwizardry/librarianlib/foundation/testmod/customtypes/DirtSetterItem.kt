package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.foundation.item.BaseItem
import com.teamwizardry.librarianlib.foundation.testmod.ModContainers
import net.minecraft.item.ItemUseContext
import net.minecraft.util.ActionResultType
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.TranslationTextComponent

class DirtSetterItem(properties: Properties): BaseItem(properties) {
    override fun onItemUse(context: ItemUseContext): ActionResultType {
        if (!context.world.isRemote) {
            ModContainers.dirtSetter.open(
                (context.player as ServerPlayerEntity?)!!,
                TranslationTextComponent("librarianlib-foundation.test_container.title"),
                context.pos
            )
        }
        return ActionResultType.SUCCESS
    }
}
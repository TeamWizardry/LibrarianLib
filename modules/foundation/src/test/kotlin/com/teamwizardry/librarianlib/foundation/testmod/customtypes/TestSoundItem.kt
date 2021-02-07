package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.core.util.kotlin.getOrNull
import com.teamwizardry.librarianlib.foundation.item.BaseItem
import com.teamwizardry.librarianlib.foundation.testmod.ModSounds
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World

class TestSoundItem(properties: Properties): BaseItem(properties) {
    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        if(worldIn.isRemote) {
            playerIn.playSound(ModSounds.testSound, 1f, 1f)
        }
        return ActionResult.resultPass(playerIn.getHeldItem(handIn))
    }
}
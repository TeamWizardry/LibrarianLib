package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World

class ItemParticleMaker : ItemMod("particle_maker") {

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        val stack = playerIn.getHeldItem(handIn)
        GuiParticleMaker().open()
        return ActionResult(EnumActionResult.SUCCESS, stack)
    }

    companion object {
        val RESOURCE = ResourceLocation(LibrarianLib.MODID, "particle_maker")
    }
}
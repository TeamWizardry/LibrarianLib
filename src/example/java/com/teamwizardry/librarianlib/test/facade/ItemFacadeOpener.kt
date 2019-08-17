package com.teamwizardry.librarianlib.test.facade

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.book.Book
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class ItemFacadeOpener : ItemMod("facade_guiopener") {

    companion object {
        val book = Book("book")
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val stack = playerIn.getHeldItem(hand)
        if(worldIn.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(GuiTestSelector())
        }
        return ActionResult(EnumActionResult.SUCCESS, stack)
    }
}

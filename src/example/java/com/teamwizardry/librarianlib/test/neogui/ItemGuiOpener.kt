package com.teamwizardry.librarianlib.test.neogui

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.neogui.provided.book.hierarchy.book.Book
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
class ItemGuiOpener : ItemMod("neo_guiopener") {

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

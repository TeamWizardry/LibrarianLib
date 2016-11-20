package com.teamwizardry.librarianlib.test.gui

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import com.teamwizardry.librarianlib.test.gui.tests.GuiTestRect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class ItemGuiOpener : ItemMod("guiopener") {

    override fun onItemRightClick(itemStackIn: ItemStack, worldIn: World, playerIn: EntityPlayer, hand: EnumHand?): ActionResult<ItemStack> {
        if(playerIn.isSneaking)
            itemStackIn.itemDamage = (itemStackIn.itemDamage + 1) % Guis.values().size
        else if(worldIn.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(Guis.values()[itemStackIn.itemDamage % Guis.values().size].create())
        }
        return ActionResult(EnumActionResult.SUCCESS, itemStackIn)
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return super.getUnlocalizedName(stack) + Guis.values()[stack.itemDamage % Guis.values().size]
    }
}

enum class Guis(val create: () -> GuiScreen) {
    RECT({ GuiTestRect() })
}

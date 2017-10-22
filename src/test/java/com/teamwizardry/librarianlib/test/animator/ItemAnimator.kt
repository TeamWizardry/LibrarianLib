package com.teamwizardry.librarianlib.test.animator

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.kotlin.localize
import com.teamwizardry.librarianlib.test.animator.tests.GuiBasicAnimation
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
class ItemAnimator : ItemMod("animator") {

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand?): ActionResult<ItemStack> {
        val stack = playerIn.getHeldItem(hand)
        if(playerIn.isSneaking)
            stack.itemDamage = (stack.itemDamage + 1) % Guis.values().size
        else if(worldIn.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(Guis.values()[stack.itemDamage % Guis.values().size].create())
        }
        return ActionResult(EnumActionResult.SUCCESS, stack)
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return (getUnlocalizedName(stack) + ".name").localize(Guis.values()[stack.itemDamage % Guis.values().size].toString())
    }
}

enum class Guis(val create: () -> GuiScreen) {
    BASIC({ GuiBasicAnimation() })
}

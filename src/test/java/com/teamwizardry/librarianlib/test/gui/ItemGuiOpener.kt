package com.teamwizardry.librarianlib.test.gui

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import com.teamwizardry.librarianlib.common.util.localize
import com.teamwizardry.librarianlib.test.gui.tests.GuiTestRect
import com.teamwizardry.librarianlib.test.gui.tests.GuiTestResizeMove
import com.teamwizardry.librarianlib.test.gui.tests.GuiTestScrolledView
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
    RECT({ GuiTestRect() }),
    MOVE({ GuiTestResizeMove() }),
    SCROLL({ GuiTestScrolledView() })
}

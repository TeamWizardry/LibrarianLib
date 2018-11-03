package com.teamwizardry.librarianlib.test.gui

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book
import com.teamwizardry.librarianlib.features.kotlin.localize
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.test.gui.tests.*
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
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

    companion object {
        val book = Book("book")
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val stack = playerIn.getHeldItem(hand)
        if(playerIn.isSneaking)
            stack.itemDamage = (stack.itemDamage + 1) % Guis.values().size
        else if(worldIn.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(Guis.values()[stack.itemDamage % Guis.values().size].create())
//            Minecraft.getMinecraft().
            playerIn.inventory.changeCurrentItem(1)
            playerIn.inventory.changeCurrentItem(-1)
        }
        return ActionResult(EnumActionResult.SUCCESS, stack)
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return (getTranslationKey(stack) + ".name").localize(Guis.values()[stack.itemDamage % Guis.values().size].toString())
    }
}

enum class Guis(val create: () -> GuiScreen) {
    RECT({ GuiTestRect() }),
    MOVE({ GuiTestResizeMove() }),
    SCALE({ GuiTestScale() }),
    SCISSOR({ GuiTestScissor() }),
    STENCIL({ GuiTestStencil() }),
    STENCIL_SPRITE({ GuiTestStencilSprite() }),
    STENCIL_MOUSEOVER({ GuiTestClippedMouseOver() }),
    AUTOSIZE({ GuiTestAutoSizeScale() }),
    SPRITE({ GuiTestSprite() }),
    MOUSE_CLICKS({ GuiTestClickEvents() }),
    MOUSE_OVER_FLAGS({ GuiTestMouseOverFlags() }),
    PROVIDED_BOOK({ GuiTestProvidedBook() }),
    LAYOUT({ GuiTestLayout() }),
    IMPLICIT_ANIMATION({ GuiTestImplicitAnimation() }),
    VALUE_ANIMATION({ GuiTestIMRMValueAnimation() }),
    CONTENT_BOUNDS({ GuiTestGetContentBounds() }),
}

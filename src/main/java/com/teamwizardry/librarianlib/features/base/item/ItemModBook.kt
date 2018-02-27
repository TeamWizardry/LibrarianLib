package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.LibLibGuiHandler
import com.teamwizardry.librarianlib.features.gui.provided.book.GuiBook
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 11:03 AM on 2/18/18.
 */
abstract class ItemModBook(name: String, vararg variants: String) : ItemMod(name, *variants) {

    @SideOnly(Side.CLIENT)
    open fun createGui(player: EntityPlayer, world: World?, stack: ItemStack): IBookGui =
            GuiBook(getBook(player, world, stack))

    abstract fun getBook(player: EntityPlayer, world: World?, stack: ItemStack): Book

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        val stack = playerIn.getHeldItem(handIn)
        playerIn.openGui(LibrarianLib, LibLibGuiHandler.ID_BOOK, worldIn, 0, 0, 0)
        return ActionResult(EnumActionResult.SUCCESS, stack)
    }

    class ItemSingleBook(name: String, val book: Book) : ItemModBook(name) {
        override fun getBook(player: EntityPlayer, world: World?, stack: ItemStack) = book
    }

    companion object {
        @JvmStatic
        @JvmName("forBook")
        operator fun invoke(name: String, book: Book) = ItemSingleBook(name, book)
    }
}

package com.teamwizardry.librarianlib.features.neogui.provided.book.hierarchy

import com.teamwizardry.librarianlib.features.neogui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.neogui.provided.book.context.Bookmark
import com.teamwizardry.librarianlib.features.neogui.provided.book.context.PaginationContext
import com.teamwizardry.librarianlib.features.neogui.provided.book.hierarchy.book.Book
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 9:35 PM on 2/19/18.
 */
interface IBookElement {
    val bookParent: Book

    @SideOnly(Side.CLIENT)
    fun createComponents(book: IBookGui): List<PaginationContext>

    @SideOnly(Side.CLIENT)
    fun addAllBookmarks(list: List<Bookmark>?): List<Bookmark> = list ?: listOf()
}

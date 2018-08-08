package com.teamwizardry.librarianlib.features.gui.provided.book.search

import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.context.PaginationContext
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 12:40 PM on 8/8/18.
 */

class SearchResults(override val bookParent: Book, val results: List<ISearchAlgorithm.Result>?) : ISearchAlgorithm.ResultAcceptor {

    @SideOnly(Side.CLIENT)
    override fun createComponents(book: IBookGui) =
            List(ComponentSearchResults.numberOfPages(results)) { PaginationContext { ComponentSearchResults(book, results, it) } }
}

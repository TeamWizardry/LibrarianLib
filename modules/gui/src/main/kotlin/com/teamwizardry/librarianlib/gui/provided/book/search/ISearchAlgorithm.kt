package com.teamwizardry.librarianlib.gui.provided.book.search

import com.teamwizardry.librarianlib.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.gui.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.gui.provided.book.hierarchy.entry.Entry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 8:52 AM on 2/26/18.
 */
@SideOnly(Side.CLIENT)
interface ISearchAlgorithm {
    fun search(input: String): List<Result>?

    fun textBoxConsumer(book: IBookGui, newComponent: (List<Result>?) -> IBookElement): (String) -> Unit {
        return { input ->

            val element = newComponent(search(input)?.sorted())

            if (book.context.bookElement is ResultAcceptor)
                book.forceInFocus(element)
            else
                book.placeInFocus(element)
        }
    }

    interface Result : Comparable<Result> {
        val frequency: Double

        val isSpecificResult: Boolean

        val entry: Entry

        override fun compareTo(other: Result) = frequency.compareTo(other.frequency)
    }

    interface ResultAcceptor : IBookElement
}

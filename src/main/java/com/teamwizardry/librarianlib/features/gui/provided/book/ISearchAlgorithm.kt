package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 8:52 AM on 2/26/18.
 */
@SideOnly(Side.CLIENT)
interface ISearchAlgorithm {
    fun search(input: String): List<Result>?

    fun textBoxConsumer(book: IBookGui, newComponent: () -> Acceptor): (String) -> Unit {
        return { input ->
            val acceptor = if (book.focus is Acceptor)
                book.focus as Acceptor
            else
                newComponent()

            acceptor.acceptResults(search(input)?.sorted())

            if (book.focus is Acceptor)
                book.forceInFocus(acceptor)
            else
                book.placeInFocus(acceptor)
        }
    }

    interface Result : Comparable<Result> {
        val frequency: Double

        val isSpecificResult: Boolean

        val entry: Entry

        override fun compareTo(other: ISearchAlgorithm.Result) = frequency.compareTo(other.frequency)
    }

    interface Acceptor : IBookElement {
        fun acceptResults(results: List<Result>?)
    }
}

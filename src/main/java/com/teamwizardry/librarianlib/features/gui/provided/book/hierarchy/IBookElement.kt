package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 9:35 PM on 2/19/18.
 */
interface IBookElement {
    val bookParent: IBookElement?

    @SideOnly(Side.CLIENT)
    fun createComponent(book: IBookGui): GuiComponent

    val heldElement get() = this
}

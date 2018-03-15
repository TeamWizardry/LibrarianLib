package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry


/**
 * Property of Demoniaque.
 * All rights reserved.
 */
class ComponentEntryPage(book: IBookGui, entry: Entry) : NavBarHolder(16, 16, book.mainBookComponent.size.xi - 32, book.mainBookComponent.size.yi - 32, book) {

    init {

        val title = entry.title.toString()

        val titleBar = ComponentSprite(book.titleBarSprite,
                (size.x / 2.0 - book.titleBarSprite.width / 2.0).toInt(),
                -pos.xi - 15)
        titleBar.color.setValue(book.book.bookColor)
        add(titleBar)

        val titleText = ComponentText((titleBar.size.x / 2.0).toInt(), (titleBar.size.y / 2.0).toInt() + 1, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE)
        titleText.text.setValue(title)
        titleBar.add(titleText)

        entry.pages
                .flatMap { it.createBookComponents(book, size) }
                .forEach { addPage(it) }
        navBar.whenMaxPagesSet()
    }
}

package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.category.Category
import net.minecraft.client.Minecraft

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
class ComponentCategoryPage(book: IBookGui, category: Category) : NavBarHolder(16, 16, book.mainBookComponent.size.xi - 32, book.mainBookComponent.size.yi - 32, book) {

    init {
        var pageComponent = ComponentVoid(0, 0, size.xi, size.yi)
        add(pageComponent)
        currentActive = pageComponent

        val itemsPerPage = 9
        var count = 0
        var id = 0
        val player = Minecraft.getMinecraft().player
        for (entry in category.entries) {
            if (entry.isUnlocked(player)) {
                val indexPlate = book.makeNavigationButton(id++, entry, null)
                pageComponent.add(indexPlate)

                count++
                if (count >= itemsPerPage) {
                    addPage(pageComponent)
                    pageComponent = ComponentVoid(0, 0, size.xi, size.yi)
                    add(pageComponent)
                    pageComponent.isVisible = false
                    count = 0
                    id = 0
                }
            }
            navBar.whenMaxPagesSet()
        }
    }
}

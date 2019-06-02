package com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.category

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.provided.book.IBookGui
import net.minecraft.client.Minecraft
import kotlin.math.ceil

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
class ComponentCategoryPage(book: IBookGui, category: Category, index: Int) : GuiComponent(16, 16, book.mainBookComponent.size.xi - 32, book.mainBookComponent.size.yi - 32) {

    companion object {
        fun numberOfPages(category: Category): Int {
            val player = Minecraft.getMinecraft().player
            return ceil(category.entries.filter { it.isUnlocked(player) }.size / 9.0).toInt()
        }
    }

    init {
        var count = -(index * 9)
        val player = Minecraft.getMinecraft().player
        for (entry in category.entries) {
            if (entry.isUnlocked(player)) {
                if (count < 0)
                    count++
                else {
                    val indexPlate = book.makeNavigationButton(count++, entry, null)
                    add(indexPlate)

                    if (count >= 9)
                        break
                }
            }
        }
    }
}

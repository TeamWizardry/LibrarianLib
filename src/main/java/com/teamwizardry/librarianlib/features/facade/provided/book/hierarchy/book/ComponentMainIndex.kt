package com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.book

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.components.ComponentSprite
import com.teamwizardry.librarianlib.features.facade.components.ComponentText
import com.teamwizardry.librarianlib.features.facade.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.category.ComponentCategoryButton
import net.minecraft.client.Minecraft
import kotlin.math.ceil

class ComponentMainIndex(book: IBookGui, page: Int) : GuiComponent(16, 16, book.mainBookComponent.size.xi, book.mainBookComponent.size.yi - 16) {

    companion object {
        fun numberOfPages(book: IBookGui): Int {
            val player = Minecraft.getMinecraft().player
            return ceil(book.book.categories.filter { it.anyUnlocked(player) }.size / 9.0).toInt()
        }
    }

    init {

        // --------- BANNER --------- //
        val componentBanner = ComponentSprite(book.bannerSprite, -24, -4)
        componentBanner.color = book.book.bookColor
        add(componentBanner)

        val fontRenderer = Minecraft.getMinecraft().fontRenderer
        val componentBannerText = ComponentText(20, 5, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
        componentBannerText.text = book.book.header.toString()
        componentBannerText.color = book.book.highlightColor

        val componentBannerSubText = ComponentText(componentBanner.size.xi - 10, 2 + fontRenderer.FONT_HEIGHT, ComponentText.TextAlignH.RIGHT, ComponentText.TextAlignV.TOP)
        componentBannerSubText.text = book.book.subtitle.toString()
        componentBannerSubText.unicode = true
        componentBannerSubText.color = book.book.highlightColor

        componentBanner.add(componentBannerText, componentBannerSubText)

        // --------- BANNER --------- //

        // --------- MAIN INDEX --------- //

        val player = Minecraft.getMinecraft().player
        var added = -(page * 9)


        val buffer = 8
        val marginX = 12
        val marginY = 39

        for (category in book.book.categories) {
            if (category.anyUnlocked(player)) {
                if (added < 0)
                    added++
                else {
                    val row = added / 3
                    val column = added % 3
                    val component = ComponentCategoryButton(marginX + column * (24 + buffer),
                            marginY + row * (24 + buffer), 24, 24, book, category)

                    add(component)
                    if (++added >= 9)
                        break
                }
            }
        }
        // --------- MAIN INDEX --------- //
    }
}

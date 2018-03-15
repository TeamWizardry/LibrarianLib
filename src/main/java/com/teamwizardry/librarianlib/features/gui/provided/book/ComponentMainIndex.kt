package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import java.util.*

class ComponentMainIndex(book: IBookGui) : NavBarHolder(0, 0, book.mainBookComponent.size.xi, book.mainBookComponent.size.yi - 16, book) {

    init {

        // --------- BANNER --------- //
        val componentBanner = ComponentSprite(book.bannerSprite, -8, 12)
        componentBanner.color.setValue(book.book.bookColor)
        add(componentBanner)

        val fontRenderer = Minecraft.getMinecraft().fontRenderer
        val componentBannerText = ComponentText(20, 5, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
        componentBannerText.text.setValue(book.book.header.toString())
        componentBannerText.color.setValue(book.book.highlightColor)

        val componentBannerSubText = ComponentText(componentBanner.size.xi - 10, 2 + fontRenderer.FONT_HEIGHT, ComponentText.TextAlignH.RIGHT, ComponentText.TextAlignV.TOP)
        componentBannerSubText.text.setValue(book.book.subtitle.toString())
        componentBannerSubText.unicode.setValue(true)
        componentBannerSubText.color.setValue(book.book.highlightColor)

        componentBanner.add(componentBannerText, componentBannerSubText)

        // --------- BANNER --------- //

        // --------- MAIN INDEX --------- //

        val categories = ArrayList<GuiComponent>()
        val player = Minecraft.getMinecraft().player
        for (category in book.book.categories) {
            if (category.anyUnlocked(player)) {
                val component = ComponentCategoryButton(0, 0, 24, 24, book, category)
                add(component)
                categories.add(component)
            }
        }

        var row = 0
        var column = 0
        val buffer = 8
        val marginX = 28
        val marginY = 45
        val itemsPerRow = 3
        for (button in categories) {
            button.pos = Vec2d(
                    (marginX + column * button.size.xi + column * buffer).toDouble(),
                    marginY.toDouble() + row * button.size.y + (row * buffer).toDouble())

            column++

            if (column >= itemsPerRow) {
                row++
                column = 0
            }
        }
        // --------- MAIN INDEX --------- //
    }
}

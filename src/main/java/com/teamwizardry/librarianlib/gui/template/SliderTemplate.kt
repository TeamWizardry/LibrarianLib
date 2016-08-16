package com.teamwizardry.librarianlib.gui.template

import com.teamwizardry.librarianlib.book.gui.GuiBook
import com.teamwizardry.librarianlib.gui.components.ComponentSliderTray
import com.teamwizardry.librarianlib.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.gui.components.ComponentSpriteTiled
import com.teamwizardry.librarianlib.gui.components.ComponentText
import net.minecraft.item.ItemStack

object SliderTemplate {

    fun text(posY: Int, text: String): ComponentSliderTray {
        val slider = ComponentSliderTray(0, posY, -120, 0)
        val textComp = ComponentText(6, 4)
        textComp.text.setValue(text)
        textComp.wrap.setValue(113)
        textComp.unicode.setValue(true)

        slider.add(ComponentSpriteTiled(GuiBook.SLIDER_NORMAL, 6, 0, 0, 133, 8 + (textComp.getLogicalSize()?.heightI() ?: 0)))
        slider.add(textComp)

        return slider
    }

    fun recipe(posY: Int, recipe: Array<Array<ItemStack>>): ComponentSliderTray {
        val slider = ComponentSliderTray(0, posY, -120, 0)
        slider.add(ComponentSprite(GuiBook.SLIDER_RECIPE, 0, 0))

        return slider
    }

}

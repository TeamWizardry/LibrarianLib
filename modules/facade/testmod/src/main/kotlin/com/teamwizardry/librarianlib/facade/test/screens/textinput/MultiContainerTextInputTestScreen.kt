package com.teamwizardry.librarianlib.facade.test.screens.textinput

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.TextInputLayer
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.layers.text.BitfontContainerLayer
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import net.minecraft.text.Text
import java.awt.Color

class MultiContainerTextInputTestScreen(title: Text): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 300, 200)
        val input = TextInputLayer(10, 10, 180, 180)
        val container = BitfontContainerLayer(180, 0, 100, 180)
        input.addContainer(container)

        input.text.insert(0, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
        input.text.insert(12, "red ", BitfontFormatting.color to Color.RED)
        input.setCursor(input.text.length)

        main.size = bg.size
        main.add(bg, input)
    }
}
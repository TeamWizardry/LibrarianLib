package com.teamwizardry.librarianlib.facade.test.screens.textinput

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.TextInputLayer
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import net.minecraft.text.Text
import java.awt.Color

class SimpleTextInputTestScreen(title: Text): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 200, 200)
        val text = TextInputLayer(10, 10, 180, 180, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
        main.size = bg.size
        main.add(bg, text)
    }
}
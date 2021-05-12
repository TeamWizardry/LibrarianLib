package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import net.minecraft.text.Text
import java.awt.Color

class OpacityTestScreen(title: Text): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 200, 100)
        main.size = bg.size
        main.add(bg)

        val outer1 = GuiLayer(10, 10, 80, 80)
        val inner1a = RectLayer(Color(0f, 1f, 0f, 0.5f), 0, 0, 60, 60)
        val inner1b = RectLayer(Color(0f, 0f, 1f, 0.5f), 20, 20, 60, 60)
        outer1.add(inner1a, inner1b)

        val outer2 = GuiLayer(110, 10, 80, 80)
        val inner2a = RectLayer(Color.GREEN, 0, 0, 60, 60)
        val inner2b = RectLayer(Color.BLUE, 20, 20, 60, 60)
        outer2.add(inner2a, inner2b)

        outer2.opacity = 0.5

        main.add(outer1, outer2)
    }
}
package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.testmod.FacadeTestScreen
import java.awt.Color

class ClipToBoundsTestScreen: FacadeTestScreen("Clip to Bounds") {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 200, 100)
        main.size = bg.size
        main.add(bg)

        val clip1 = GuiLayer(30, 40, 30, 30)
        clip1.clipToBounds = true
        val clipped1 = RectLayer(Color.MAGENTA, -10, -10, 50, 50)
        val nonClipped1 = RectLayer(Color.GREEN, 0, 0, 30, 30)
        clip1.add(clipped1, nonClipped1)
        main.add(clip1)

        val clip2 = GuiLayer(20, -20, 30, 30)
        clip2.clipToBounds = true
        val clipped2 = RectLayer(Color.MAGENTA, -10, -10, 50, 50)
        val nonClipped2 = RectLayer(Color.BLUE, 0, 0, 30, 30)
        clip2.add(clipped2, nonClipped2)
        clip1.add(clip2)

        val clip3 = GuiLayer(125, 40, 30, 30)
        clip3.clipToBounds = true
        clip3.cornerRadius = 10.0
        val clipped3 = RectLayer(Color.MAGENTA, -10, -10, 50, 50)
        val nonClipped3 = RectLayer(Color.BLUE, 0, 0, 30, 30)
        clip3.add(clipped3, nonClipped3)
        main.add(clip3)

    }
}
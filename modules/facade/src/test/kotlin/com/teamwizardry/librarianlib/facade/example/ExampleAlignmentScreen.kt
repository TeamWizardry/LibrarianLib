package com.teamwizardry.librarianlib.facade.example

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.supporting.DisplaySpace
import com.teamwizardry.librarianlib.facade.layer.supporting.ScreenSpace
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import net.minecraft.text.Text
import java.awt.Color

class ExampleAlignmentScreen(title: Text): FacadeScreen(title) {
    init {
        main.size = ScreenSpace.convertOffsetFrom(vec(750, 422), DisplaySpace)
        val bg = RectLayer(Color.WHITE, 0, 0)
        bg.size = main.size
        main.add(bg)
    }
}
package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.testmod.FacadeTestScreen
import com.teamwizardry.librarianlib.math.vec
import java.awt.Color

/**
 * A simple yoga list, with a button to add elements and clicking elements deletes them
 */
class YogaListScreen: FacadeTestScreen("Yoga List") {
    init {
        val bg = RectLayer(Color(255, 255, 255, 127), 0, 0, 100, 500)
        main.add(bg)
        main.size = vec(100, 500)
        main.yoga()
            .padding.px(10f)
            .flexDirection.column()

        val button = RectLayer(Color.GREEN, -20, 0, 20, 20)
        main.add(button)
        var i = 0
        button.hook<GuiLayerEvents.MouseClick> {
            val box = RectLayer(DistinctColors.colors[i++ % DistinctColors.colors.size], 0, 0, 80, 80)
            box.yoga()
                .marginBottom.px(5f)
                .flexBasis.px(80f)
            box.hook<GuiLayerEvents.MouseClick> {
                box.removeFromParent()
            }
            main.add(box)
        }
    }
}
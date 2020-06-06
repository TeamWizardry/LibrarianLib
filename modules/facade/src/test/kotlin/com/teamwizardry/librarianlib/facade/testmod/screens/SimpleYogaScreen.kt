package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.testmod.FacadeTestScreen
import com.teamwizardry.librarianlib.math.vec
import java.awt.Color

/**
 * A simple yoga screen, configured similarly to the https://yogalayout.com front page example
 */
class SimpleYogaScreen: FacadeTestScreen("Yoga Simple Flex") {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 500, 500)
        main.add(bg)
        main.size = vec(500, 500)
        main.yoga()
            .padding.px(20f)

        val box1 = RectLayer(Color.RED, 0, 0, 100, 100)
        box1.yoga()

        val box2 = RectLayer(Color.RED, 0, 0, 100, 100)
        box2.yoga()
            .flex(1f)
            .marginHorizontal.px(20f)

        val box3 = RectLayer(Color.RED, 0, 0, 100, 100)
        box3.yoga()
        main.add(box1, box2, box3)

    }
}
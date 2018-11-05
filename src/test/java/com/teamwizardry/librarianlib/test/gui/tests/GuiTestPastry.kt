package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryBackground
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryButton
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestPastry : GuiBase() {
    init {
        main.size = vec(300, 300)

        main.add(PastryBackground(0, 0, 300, 300))

        var button = PastryButton(5, 5, 100)
        button.label.text = "Push me! gg."
        main.add(button)
        button = PastryButton(5, 25, 100)
        button.label.text = "I may or may not be a bit too long to fit."
        main.add(button)

    }
}

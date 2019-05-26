package com.teamwizardry.librarianlib.test.neogui.tests

import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.layers.TextLayer
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastryTextEditor
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.helpers.vec

/**
 * Created by TheCodeWarrior
 */
class GuiTestTextField : GuiBase() {
    init {
        main.size = vec(150, 150)

        val background = PastryBackground(0, 0, 150, 150)
        main.add(background)

        main.add(TextLayer(4, 4, "§2Compose message:"))

        val field = PastryTextEditor(4, 14, 148, 138)
        main.add(field)
    }
}

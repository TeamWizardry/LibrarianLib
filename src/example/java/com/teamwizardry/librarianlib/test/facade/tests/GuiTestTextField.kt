package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryTextEditor
import com.teamwizardry.librarianlib.features.facade.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.helpers.vec
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

/**
 * Created by TheCodeWarrior
 */
@UseExperimental(ExperimentalBitfont::class)
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

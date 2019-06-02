package com.teamwizardry.librarianlib.test.facade.pastry.tests

import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.test.facade.pastry.PastryTestBase
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

@UseExperimental(ExperimentalBitfont::class)
class PastryTestButton: PastryTestBase() {
    init {
        this.stack.add(PastryButton("Truncated text", 0, 0, 50))
        this.stack.add(PastryButton("Short text", 0, 0, 100))
        this.stack.add(PastryButton("Auto-width text", 0, 0))
        this.stack.add(PastryButton("Tall button", 0, 0, 100, 25))
    }
}
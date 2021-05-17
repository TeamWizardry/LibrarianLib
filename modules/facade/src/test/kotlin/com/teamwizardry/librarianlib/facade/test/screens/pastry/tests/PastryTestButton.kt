package com.teamwizardry.librarianlib.facade.test.screens.pastry.tests

import com.teamwizardry.librarianlib.facade.pastry.layers.PastryButton
import com.teamwizardry.librarianlib.facade.test.screens.pastry.PastryTestBase

class PastryTestButton: PastryTestBase() {
    init {
        this.stack.add(PastryButton("Truncated text", 0, 0, 50))
        this.stack.add(PastryButton("Short text", 0, 0, 100))
        this.stack.add(PastryButton("Auto-width text", 0, 0))
        this.stack.add(PastryButton("Tall button", 0, 0, 100, 25))
    }
}
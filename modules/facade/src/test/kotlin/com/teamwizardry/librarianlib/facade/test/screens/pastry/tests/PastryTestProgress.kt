package com.teamwizardry.librarianlib.facade.test.screens.pastry.tests

import com.teamwizardry.librarianlib.facade.pastry.layers.PastryProgressBar
import com.teamwizardry.librarianlib.facade.test.screens.pastry.PastryTestBase
import com.teamwizardry.librarianlib.math.Easing

class PastryTestProgress: PastryTestBase() {
    init {
        val progress = PastryProgressBar(10, 35, 75)
        progress.progress_im.animateKeyframes(0.0)
            .add(80f, Easing.easeOutCubic, 1.0)
            .hold(10f)
            .add(40f, Easing.easeOutBounce, 0.0)
            .hold(10f)
            .repeat()
        this.stack.add(progress)
    }
}
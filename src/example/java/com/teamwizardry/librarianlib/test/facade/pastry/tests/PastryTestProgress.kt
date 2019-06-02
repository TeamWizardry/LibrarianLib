package com.teamwizardry.librarianlib.test.facade.pastry.tests

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.facade.provided.pastry.layers.PastryProgressBar
import com.teamwizardry.librarianlib.test.facade.pastry.PastryTestBase
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

@UseExperimental(ExperimentalBitfont::class)
class PastryTestProgress: PastryTestBase() {
    init {
        val progress = PastryProgressBar(10, 35, 75, 5)
        progress.progress_im.animateKeyframes(0.0)
            .add(80f, 1.0, Easing.easeOutCubic)
            .add(10f, 1.0)
            .add(40f, 0.0, Easing.easeOutBounce)
            .add(10f, 0.0)
            .finish().repeatCount = -1
        this.stack.add(progress)
    }
}
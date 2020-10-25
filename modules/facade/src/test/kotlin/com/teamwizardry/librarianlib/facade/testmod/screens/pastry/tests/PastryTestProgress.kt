package com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests

import com.teamwizardry.librarianlib.facade.pastry.layers.PastryProgressBar
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.PastryTestBase

class PastryTestProgress: PastryTestBase() {
    init {
        val progress = PastryProgressBar(10, 35, 75, 5)
        progress.progress_im.animate(1.0, 80f).repeat().reverseOnRepeat()
//        progress.progress_im.animateKeyframes(0.0)
//            .add(80f, 1.0, Easing.easeOutCubic)
//            .add(10f, 1.0)
//            .add(40f, 0.0, Easing.easeOutBounce)
//            .add(10f, 0.0)
//            .finish().repeatCount = -1
        this.stack.add(progress)
    }
}
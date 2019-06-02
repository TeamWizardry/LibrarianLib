package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.layers.ColorLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestValueKeyframeBuilder : GuiBase() {
    init {
        main.size = vec(100, 100)

        val layer = ColorLayer(Color.RED, 0, 0, 10, 10)
        layer.anchor = vec(0.5, 0.5)

        layer.pos_rm.animateKeyframes(vec(0, 0))
            .add(60f, vec(100, 0), Easing.easeOutBounce)
            .add(60f, vec(100, 100), Easing.easeOutBounce)
            .add(60f, vec(0, 100), Easing.easeOutBounce)
            .add(60f, vec(0, 0), Easing.easeOutBounce)
            .finish().repeatCount = -1

        main.add(ColorLayer(Color.WHITE, 0, 0, 100, 100), layer)
    }
}

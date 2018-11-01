package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Sprite

/**
 * Created by TheCodeWarrior
 */
class GuiTestStencilSprite : GuiBase() {
    init {
        main.size = vec(0, 0)

        val wrapper = ComponentVoid(0, 0)

        val clipping = ComponentVoid(0, 0, 50, 50)
        val clipped = ComponentSprite(Sprite("minecraft:textures/blocks/dirt.png".toRl()), -25, -25, 100, 100)

        wrapper.add(clipping)
        clipping.add(clipped)

        clipping.clipToBounds = true
        clipping.clippingSprite = Sprite("minecraft:textures/items/diamond_sword.png".toRl())

        main.add(wrapper)
    }
}

package com.teamwizardry.librarianlib.test.neogui.tests

import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Sprite

/**
 * Created by TheCodeWarrior
 */
class GuiTestStencilSprite : GuiBase() {
    init {
        main.size = vec(0, 0)

        val wrapper = GuiComponent(0, 0)

        val clipping = GuiComponent(0, 0, 50, 50)
        val clipped = ComponentSprite(Sprite("minecraft:textures/blocks/dirt.png".toRl()), -25, -25, 100, 100)

        wrapper.add(clipping)
        clipping.add(clipped)

        clipping.clipToBounds = true
        clipping.clippingSprite = Sprite("minecraft:textures/items/diamond_sword.png".toRl())

        main.add(wrapper)
    }
}

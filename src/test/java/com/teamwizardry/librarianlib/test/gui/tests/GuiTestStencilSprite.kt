package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.LTextureAtlasSprite
import com.teamwizardry.librarianlib.features.sprite.Sprite
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestStencilSprite : GuiBase(0, 0) {
    init {
        val wrapper = ComponentVoid(0, 0)

        val clipping = ComponentVoid(0, 0, 50, 50)
        val clipped = ComponentSprite(Sprite("minecraft:textures/blocks/dirt.png".toRl()), -25, -25, 100, 100)

        wrapper.add(clipping)
        clipping.add(clipped)

        clipping.clipping.clipToBounds = true
        clipping.clipping.clippingSprite = Sprite("minecraft:textures/items/diamond_sword.png".toRl())

        mainComponents.add(wrapper)
    }
}

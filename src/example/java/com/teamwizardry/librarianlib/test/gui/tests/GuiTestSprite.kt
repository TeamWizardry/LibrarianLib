package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.util.ResourceLocation

/**
 * Created by TheCodeWarrior
 */
class GuiTestSprite : GuiBase(100, 100) {
    init {
        val sprite = Sprite(ResourceLocation("textures/blocks/glass_yellow.png"))
        val c = ComponentSprite(sprite, 25, 25, 50, 50)
        mainComponents.add(c)

    }
}

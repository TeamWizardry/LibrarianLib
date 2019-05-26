package com.teamwizardry.librarianlib.test.neogui.tests

import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.util.ResourceLocation

/**
 * Created by TheCodeWarrior
 */
class GuiTestSprite : GuiBase() {
    init {
        main.size = vec(100, 100)
        val sprite = Sprite(ResourceLocation("textures/blocks/glass_yellow.png"))
        val c = ComponentSprite(sprite, 25, 25, 50, 50)
        main.add(c)

    }
}

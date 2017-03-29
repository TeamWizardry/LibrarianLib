package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.util.ResourceLocation

class GuiStyle(val TEXTURE: Texture) {
    val BUTTON: Sprite
    val BUTTON_BORDER = 4

    init {

        BUTTON = TEXTURE.getSprite("button", 32, 32)
    }

    companion object {

        val NORMAL = GuiStyle(Texture(ResourceLocation(LibrarianLib.MODID, "textures/styles/normal.png")))
    }
}

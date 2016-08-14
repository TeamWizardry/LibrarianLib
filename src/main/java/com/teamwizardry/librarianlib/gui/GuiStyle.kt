package com.teamwizardry.librarianlib.gui

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.sprite.Sprite
import com.teamwizardry.librarianlib.sprite.Texture
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

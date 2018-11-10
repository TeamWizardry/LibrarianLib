package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture

object PastryTexture {
    val texture = Texture("librarianlib:textures/gui/pastry/pastry.png".toRl(), 256, 256)

    val defaultBackground = texture.getSprite("background.default")
    val lightBackground = texture.getSprite("background.light")
    val darkBackground = texture.getSprite("background.dark")
    val button = texture.getSprite("button.unpressed")
    val buttonPressed = texture.getSprite("button.pressed")
    val switchOff = texture.getSprite("switch.off")
    val switchOn = texture.getSprite("switch.on")
    val switchHandle = texture.getSprite("switch.handle")
    val checkboxOff = texture.getSprite("checkbox.off")
    val checkboxOn = texture.getSprite("checkbox.on")
    val radioOff = texture.getSprite("radiobutton.off")
    val radioOn = texture.getSprite("radiobutton.on")
}

enum class BackgroundType(val sprite: Sprite) {
    DEFAULT(PastryTexture.defaultBackground),
    LIGHT(PastryTexture.lightBackground),
    DARK(PastryTexture.darkBackground)
}

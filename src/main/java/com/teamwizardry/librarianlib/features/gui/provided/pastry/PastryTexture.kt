package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture

object PastryTexture {
    val texture = Texture("librarianlib:textures/gui/pastry/light.png".toRl(), 256, 256)
    val shadowTexture = Texture("librarianlib:textures/gui/pastry/shadow.png".toRl(), 256, 256)
    init {
        shadowTexture.enableBlending()
    }

    val background = texture.getSprite("background")
    val backgroundInnerCorners = texture.getSprite("background.inner_corners")
    val backgroundSlightRound = texture.getSprite("background.slight.round")
    val backgroundSlightRoundInnerCorners = texture.getSprite("background.slight.round.inner_corners")
    val backgroundSlight = texture.getSprite("background.slight")
    val backgroundSlightInnerCorners = texture.getSprite("background.slight.inner_corners")
    val backgroundSlightInset = texture.getSprite("background.slight.inset")
    val backgroundSlightInsetInnerCorners = texture.getSprite("background.slight.inset.inner_corners")
    val backgroundBlack = texture.getSprite("background.black")
    val backgroundBlackInnerCorners = texture.getSprite("background.black.inner_corners")

    val textfield = texture.getSprite("textfield")
    val dropdown = texture.getSprite("dropdown")
    val dropdownBackground = texture.getSprite("dropdown.background")
    val dropdownHighlight = texture.getSprite("dropdown.highlight")
    val dropdownSeparator = texture.getSprite("dropdown.separator")

    val scrollbarHandleHorizontal = texture.getSprite("scrollbar.handle.horizontal")
    val scrollbarHandleVertical = texture.getSprite("scrollbar.handle.vertical")
    val scrollbarTrack = texture.getSprite("scrollbar.track")

    val button = texture.getSprite("button")
    val buttonPressed = texture.getSprite("button.pressed")
    val switchOff = texture.getSprite("switch.off")
    val switchOn = texture.getSprite("switch.on")
    val switchHandle = texture.getSprite("switch.handle")
    val radioButton = texture.getSprite("radio_button")
    val checkbox = texture.getSprite("checkbox")
    val progressbar = texture.getSprite("progressbar")
    val progressbarFill = texture.getSprite("progressbar.fill")
    val sliderHandle = texture.getSprite("slider.handle")
    val sliderHandleRight = texture.getSprite("slider.handle.right")
    val sliderHandleLeft = texture.getSprite("slider.handle.left")
    val sliderHandleDown = texture.getSprite("slider.handle.down")
    val sliderHandleUp = texture.getSprite("slider.handle.up")

    val shadowColor = texture.getColor("window.shadow.color")
    val sliderLinesColor = texture.getColor("slider.lines")
    val sliderLinesHighlightedColor = texture.getColor("slider.lines.highlighted")

    val shadowFadeSize = 48
    val shadowSprite = shadowTexture.getSprite("shadow")
}

enum class BackgroundTexture(val background: Sprite, val innerCorners: Sprite) {
    DEFAULT(PastryTexture.background, PastryTexture.backgroundInnerCorners),
    SLIGHT(PastryTexture.backgroundSlight, PastryTexture.backgroundSlightInnerCorners),
    SLIGHT_INSET(PastryTexture.backgroundSlight, PastryTexture.backgroundSlightInnerCorners),
    SLIGHT_ROUND(PastryTexture.backgroundSlightRound, PastryTexture.backgroundSlightRoundInnerCorners),
    BLACK(PastryTexture.backgroundBlack, PastryTexture.backgroundBlackInnerCorners)
}

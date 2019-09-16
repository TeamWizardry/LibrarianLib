package com.teamwizardry.librarianlib.features.facade.provided.pastry

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.util.ResourceLocation
import java.awt.Color

object PastryTexture {

    var theme: Theme = Theme.NORMAL
        set(value) {
            field = value
            texture.switchTexture(value.location)
        }

    val texture = Texture(Theme.NORMAL.location, 256, 256)
    val shadowTexture = Texture("librarianlib:textures/gui/pastry/shadow.png".toRl(), 256, 256)
    init {
        shadowTexture.enableBlending()
    }

    val background: Sprite by texture.sprites
    val backgroundInnerCorners: Sprite by texture.sprites
    val backgroundSlightRound: Sprite by texture.sprites
    val backgroundSlightRoundInnerCorners: Sprite by texture.sprites
    val backgroundSlight: Sprite by texture.sprites
    val backgroundSlightInnerCorners: Sprite by texture.sprites
    val backgroundSlightInset: Sprite by texture.sprites
    val backgroundSlightInsetInnerCorners: Sprite by texture.sprites
    val backgroundBlack: Sprite by texture.sprites
    val backgroundBlackInnerCorners: Sprite by texture.sprites

    val windowBackgroundTitlebar: Sprite by texture.sprites
    val windowBackgroundBody: Sprite by texture.sprites
    val windowDialogBackgroundTitlebar: Sprite by texture.sprites
    val windowDialogBackgroundBody: Sprite by texture.sprites
    val windowSlightBackgroundTitlebar: Sprite by texture.sprites
    val windowSlightBackgroundBody: Sprite by texture.sprites

    val windowIconsCloseMacosSmall: Sprite by texture.sprites
    val windowIconsMinimizeMacosSmall: Sprite by texture.sprites
    val windowIconsMaximizeMacosSmall: Sprite by texture.sprites
    val windowIconsCloseMacos: Sprite by texture.sprites
    val windowIconsMinimizeMacos: Sprite by texture.sprites
    val windowIconsMaximizeMacos: Sprite by texture.sprites
    val windowIconsMinimize: Sprite by texture.sprites
    val windowIconsMaximize: Sprite by texture.sprites
    val windowIconsClose: Sprite by texture.sprites

    val tabsButton: Sprite by texture.sprites
    val tabsBody: Sprite by texture.sprites
    val tabsButtonPressed: Sprite by texture.sprites
    val splitpaneRegion: Sprite by texture.sprites
    val splitpaneHsplit: Sprite by texture.sprites
    val splitpaneVsplit: Sprite by texture.sprites


    val textfield: Sprite by texture.sprites
    val dropdown: Sprite by texture.sprites
    val dropdownBackground: Sprite by texture.sprites
    val dropdownHighlight: Sprite by texture.sprites
    val dropdownSeparator: Sprite by texture.sprites

    val scrollbarHandleHorizontal: Sprite by texture.sprites
    val scrollbarHandleHorizontalDashes: Sprite by texture.sprites
    val scrollbarHandleVertical: Sprite by texture.sprites
    val scrollbarHandleVerticalDashes: Sprite by texture.sprites
    val scrollbarTrack: Sprite by texture.sprites

    val button: Sprite by texture.sprites
    val buttonPressed: Sprite by texture.sprites
    val switchOff: Sprite by texture.sprites
    val switchOn: Sprite by texture.sprites
    val switchHandle: Sprite by texture.sprites
    val radioButton: Sprite by texture.sprites
    val checkbox: Sprite by texture.sprites
    val progressbar: Sprite by texture.sprites
    val progressbarFill: Sprite by texture.sprites
    val sliderHandle: Sprite by texture.sprites
    val sliderHandleRight: Sprite by texture.sprites
    val sliderHandleLeft: Sprite by texture.sprites
    val sliderHandleDown: Sprite by texture.sprites
    val sliderHandleUp: Sprite by texture.sprites

    val windowShadowColor: Color by texture.colors
    val sliderLinesColor: Color by texture.colors
    val sliderLinesHighlightedColor: Color by texture.colors

    val shadowFadeSize = 48
    val shadowSprite = shadowTexture.getSprite("shadow")

    enum class Theme(val location: ResourceLocation) {
        NORMAL("librarianlib:textures/gui/pastry/light.png".toRl()),
        DARK("librarianlib:textures/gui/pastry/dark.png".toRl()),
        HIGH_CONTRAST("librarianlib:textures/gui/pastry/contrast.png".toRl()),
    }
}

enum class BackgroundTexture(val background: Sprite, val innerCorners: Sprite) {
    DEFAULT(PastryTexture.background, PastryTexture.backgroundInnerCorners),
    SLIGHT(PastryTexture.backgroundSlight, PastryTexture.backgroundSlightInnerCorners),
    SLIGHT_INSET(PastryTexture.backgroundSlightInset, PastryTexture.backgroundSlightInsetInnerCorners),
    SLIGHT_ROUND(PastryTexture.backgroundSlightRound, PastryTexture.backgroundSlightRoundInnerCorners),
    BLACK(PastryTexture.backgroundBlack, PastryTexture.backgroundBlackInnerCorners)
}

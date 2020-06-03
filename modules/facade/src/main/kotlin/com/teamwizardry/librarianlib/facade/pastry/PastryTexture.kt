package com.teamwizardry.librarianlib.facade.pastry

import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.mosaic.Mosaic
import com.teamwizardry.librarianlib.mosaic.Sprite
import net.minecraft.util.ResourceLocation
import java.awt.Color

object PastryTexture {

    var theme: Theme = Theme.NORMAL
        set(value) {
            field = value
            texture.switchTexture(value.location)
        }

    val texture = Mosaic(Theme.NORMAL.location, 256, 256)
    val shadowTexture = Mosaic("librarianlib:facade/textures/pastry/shadow.png".toRl(), 256, 256)

    val background: Sprite by texture.delegate
    val backgroundInnerCorners: Sprite by texture.delegate
    val backgroundSlightRound: Sprite by texture.delegate
    val backgroundSlightRoundInnerCorners: Sprite by texture.delegate
    val backgroundSlight: Sprite by texture.delegate
    val backgroundSlightInnerCorners: Sprite by texture.delegate
    val backgroundSlightInset: Sprite by texture.delegate
    val backgroundSlightInsetInnerCorners: Sprite by texture.delegate
    val backgroundBlack: Sprite by texture.delegate
    val backgroundBlackInnerCorners: Sprite by texture.delegate

    val windowBackgroundTitlebar: Sprite by texture.delegate
    val windowBackgroundBody: Sprite by texture.delegate
    val windowDialogBackgroundTitlebar: Sprite by texture.delegate
    val windowDialogBackgroundBody: Sprite by texture.delegate
    val windowSlightBackgroundTitlebar: Sprite by texture.delegate
    val windowSlightBackgroundBody: Sprite by texture.delegate

    val windowIconsCloseMacosSmall: Sprite by texture.delegate
    val windowIconsMinimizeMacosSmall: Sprite by texture.delegate
    val windowIconsMaximizeMacosSmall: Sprite by texture.delegate
    val windowIconsCloseMacos: Sprite by texture.delegate
    val windowIconsMinimizeMacos: Sprite by texture.delegate
    val windowIconsMaximizeMacos: Sprite by texture.delegate
    val windowIconsMinimize: Sprite by texture.delegate
    val windowIconsMaximize: Sprite by texture.delegate
    val windowIconsClose: Sprite by texture.delegate

    val tabsButton: Sprite by texture.delegate
    val tabsBody: Sprite by texture.delegate
    val tabsButtonPressed: Sprite by texture.delegate
    val splitpaneRegion: Sprite by texture.delegate
    val splitpaneHsplit: Sprite by texture.delegate
    val splitpaneVsplit: Sprite by texture.delegate


    val textfield: Sprite by texture.delegate
    val dropdown: Sprite by texture.delegate
    val dropdownBackground: Sprite by texture.delegate
    val dropdownHighlight: Sprite by texture.delegate
    val dropdownSeparator: Sprite by texture.delegate

    val scrollbarHandleHorizontal: Sprite by texture.delegate
    val scrollbarHandleHorizontalDashes: Sprite by texture.delegate
    val scrollbarHandleVertical: Sprite by texture.delegate
    val scrollbarHandleVerticalDashes: Sprite by texture.delegate
    val scrollbarTrack: Sprite by texture.delegate

    val button: Sprite by texture.delegate
    val buttonPressed: Sprite by texture.delegate
    val switchOff: Sprite by texture.delegate
    val switchOn: Sprite by texture.delegate
    val switchHandle: Sprite by texture.delegate
    val radioButton: Sprite by texture.delegate
    val checkbox: Sprite by texture.delegate
    val progressbar: Sprite by texture.delegate
    val progressbarFill: Sprite by texture.delegate
    val sliderHandle: Sprite by texture.delegate
    val sliderHandleRight: Sprite by texture.delegate
    val sliderHandleLeft: Sprite by texture.delegate
    val sliderHandleDown: Sprite by texture.delegate
    val sliderHandleUp: Sprite by texture.delegate

    val shadowFadeSize = 48
    val shadowSprite = shadowTexture.getSprite("shadow")

    enum class Theme(val location: ResourceLocation) {
        NORMAL("librarianlib:facade/textures/pastry/light.png".toRl()),
        DARK("librarianlib:facade/textures/pastry/dark.png".toRl()),
        HIGH_CONTRAST("librarianlib:facade/textures/pastry/contrast.png".toRl()),
    }
}

enum class BackgroundTexture(val background: Sprite, val innerCorners: Sprite) {
    DEFAULT(PastryTexture.background, PastryTexture.backgroundInnerCorners),
    SLIGHT(PastryTexture.backgroundSlight, PastryTexture.backgroundSlightInnerCorners),
    SLIGHT_INSET(PastryTexture.backgroundSlightInset, PastryTexture.backgroundSlightInsetInnerCorners),
    SLIGHT_ROUND(PastryTexture.backgroundSlightRound, PastryTexture.backgroundSlightRoundInnerCorners),
    BLACK(PastryTexture.backgroundBlack, PastryTexture.backgroundBlackInnerCorners)
}

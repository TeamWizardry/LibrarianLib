package com.teamwizardry.librarianlib.facade.pastry

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.mosaic.Mosaic
import com.teamwizardry.librarianlib.mosaic.Sprite
import net.minecraft.util.ResourceLocation
import java.awt.Color

internal object PastryTexture {

//    var theme: Theme = Theme.NORMAL
    // TODO: theme switching
//        set(value) {
//            field = value
//            texture.switchTexture(value.location)
//        }

    val texture = Mosaic(Theme.NORMAL.location, 256, 256)
    val shadowTexture = Mosaic(loc("librarianlib:facade/textures/pastry/shadow.png"), 256, 256)

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
    val backgroundInput: Sprite by texture.delegate
    val backgroundInputInnerCorners: Sprite by texture.delegate

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
        NORMAL(loc("librarianlib:facade/textures/pastry/light.png")),
        DARK(loc("librarianlib:facade/textures/pastry/dark.png")),
        HIGH_CONTRAST(loc("librarianlib:facade/textures/pastry/contrast.png")),
    }
}

public enum class BackgroundTexture(internal val background: Sprite, internal val innerCorners: Sprite) {
    DEFAULT(PastryTexture.background, PastryTexture.backgroundInnerCorners),
    SLIGHT(PastryTexture.backgroundSlight, PastryTexture.backgroundSlightInnerCorners),
    SLIGHT_INSET(PastryTexture.backgroundSlightInset, PastryTexture.backgroundSlightInsetInnerCorners),
    SLIGHT_ROUND(PastryTexture.backgroundSlightRound, PastryTexture.backgroundSlightRoundInnerCorners),
    BLACK(PastryTexture.backgroundBlack, PastryTexture.backgroundBlackInnerCorners),
    INPUT(PastryTexture.backgroundInput, PastryTexture.backgroundInputInnerCorners)
}

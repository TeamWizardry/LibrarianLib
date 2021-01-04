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

    val vanillaBackground: Sprite by texture.delegate
    val vanillaBackgroundEdges: Sprite by texture.delegate
    val thinVanillaBackground: Sprite by texture.delegate
    val thinVanillaBackgroundEdges: Sprite by texture.delegate
    val lightRoundBackground: Sprite by texture.delegate
    val lightRoundBackgroundEdges: Sprite by texture.delegate
    val lightSquareBackground: Sprite by texture.delegate
    val lightSquareBackgroundEdges: Sprite by texture.delegate
    val lightInsetBackground: Sprite by texture.delegate
    val lightInsetBackgroundEdges: Sprite by texture.delegate
    val blackRoundBackground: Sprite by texture.delegate
    val blackRoundBackgroundEdges: Sprite by texture.delegate
    val blackSquareBackground: Sprite by texture.delegate
    val blackSquareBackgroundEdges: Sprite by texture.delegate
    val blackInsetBackground: Sprite by texture.delegate
    val blackInsetBackgroundEdges: Sprite by texture.delegate
    val inputBackground: Sprite by texture.delegate
    val inputBackgroundEdges: Sprite by texture.delegate

    val tabsButton: Sprite by texture.delegate
    val tabsBody: Sprite by texture.delegate
    val tabsButtonPressed: Sprite by texture.delegate
    val splitpaneRegion: Sprite by texture.delegate
    val splitpaneHsplit: Sprite by texture.delegate
    val splitpaneVsplit: Sprite by texture.delegate

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

/**
 * A background for use in one of the pastry background layers.
 */
public interface IBackgroundStyle {
    /**
     * A simple square background configured with 9-slicing
     */
    public val background: Sprite

    /**
     * A CTM-esque sprite for the dynamic background. The contents should be a 4x8 grid, where each cell is a square
     * [edgeSize] pixels across.
     *
     * See the files in `assets/librarianlib/facade/textures/pastry` for examples of the sprite sheet
     */
    public val edges: Sprite

    /**
     * The size in pixels of the [edges]
     */
    public val edgeSize: Double

    /**
     * The number of pixels to inset the [edges] when rendering. Effectively reduces [edgeSize] by this amount.
     */
    public val edgeInset: Double
}

public enum class PastryBackgroundStyle(
    override val background: Sprite,
    override val edges: Sprite,
    override val edgeSize: Double,
    override val edgeInset: Double
): IBackgroundStyle {

    /**
     * The background style used by vanilla Minecraft
     */
    VANILLA(PastryTexture.vanillaBackground, PastryTexture.vanillaBackgroundEdges, 4.0, 1.0),

    /**
     * Similar to [VANILLA] but thinner
     */
    THIN_VANILLA(PastryTexture.thinVanillaBackground, PastryTexture.thinVanillaBackgroundEdges, 2.0, 0.0),

    LIGHT_ROUND(PastryTexture.lightRoundBackground, PastryTexture.lightRoundBackgroundEdges, 2.0, 0.0),
    LIGHT_SQUARE(PastryTexture.lightSquareBackground, PastryTexture.lightSquareBackgroundEdges, 2.0, 0.0),
    LIGHT_INSET(PastryTexture.lightInsetBackground, PastryTexture.lightInsetBackgroundEdges, 2.0, 0.0),
    BLACK_ROUND(PastryTexture.blackRoundBackground, PastryTexture.blackRoundBackgroundEdges, 2.0, 0.0),
    BLACK_SQUARE(PastryTexture.blackSquareBackground, PastryTexture.blackSquareBackgroundEdges, 2.0, 0.0),
    BLACK_INSET(PastryTexture.blackInsetBackground, PastryTexture.blackInsetBackgroundEdges, 2.0, 0.0),

    /**
     * The background style used by vanilla input wells. e.g. slots and text fields.
     */
    INPUT(PastryTexture.inputBackground, PastryTexture.inputBackgroundEdges, 2.0, 0.0),
    ;
}
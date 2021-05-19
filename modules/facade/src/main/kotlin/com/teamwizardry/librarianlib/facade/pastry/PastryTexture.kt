package com.teamwizardry.librarianlib.facade.pastry

import com.teamwizardry.librarianlib.mosaic.Mosaic
import com.teamwizardry.librarianlib.mosaic.Sprite
import net.minecraft.util.Identifier
import java.awt.Color

internal object PastryTexture {

//    var theme: Theme = Theme.NORMAL
    // TODO: theme switching
//        set(value) {
//            field = value
//            texture.switchTexture(value.location)
//        }

    val texture = Mosaic(Theme.NORMAL.location, 256, 256)
    val shadowTexture = Mosaic(Identifier("liblib-facade:textures/pastry/shadow.png"), 256, 256)

    val vanillaBackground: Sprite by texture
    val vanillaBackgroundEdges: Sprite by texture
    val thinVanillaBackground: Sprite by texture
    val thinVanillaBackgroundEdges: Sprite by texture
    val lightRoundBackground: Sprite by texture
    val lightRoundBackgroundEdges: Sprite by texture
    val lightSquareBackground: Sprite by texture
    val lightSquareBackgroundEdges: Sprite by texture
    val lightInsetBackground: Sprite by texture
    val lightInsetBackgroundEdges: Sprite by texture
    val blackRoundBackground: Sprite by texture
    val blackRoundBackgroundEdges: Sprite by texture
    val blackSquareBackground: Sprite by texture
    val blackSquareBackgroundEdges: Sprite by texture
    val blackInsetBackground: Sprite by texture
    val blackInsetBackgroundEdges: Sprite by texture
    val inputBackground: Sprite by texture
    val inputBackgroundEdges: Sprite by texture

    val tabsButton: Sprite by texture
    val tabsBody: Sprite by texture
    val tabsButtonPressed: Sprite by texture
    val splitpaneRegion: Sprite by texture
    val splitpaneHsplit: Sprite by texture
    val splitpaneVsplit: Sprite by texture

    val dropdown: Sprite by texture
    val dropdownBackground: Sprite by texture
    val dropdownHighlight: Sprite by texture
    val dropdownSeparator: Sprite by texture

    val scrollbarHandleHorizontal: Sprite by texture
    val scrollbarHandleHorizontalDashes: Sprite by texture
    val scrollbarHandleVertical: Sprite by texture
    val scrollbarHandleVerticalDashes: Sprite by texture
    val scrollbarTrack: Sprite by texture

    val button: Sprite by texture
    val buttonPressed: Sprite by texture
    val switchOff: Sprite by texture
    val switchOn: Sprite by texture
    val switchHandle: Sprite by texture
    val radioButton: Sprite by texture
    val checkbox: Sprite by texture
    val progressbar: Sprite by texture
    val progressbarFill: Sprite by texture
    val sliderHandle: Sprite by texture
    val sliderHandleRight: Sprite by texture
    val sliderHandleLeft: Sprite by texture
    val sliderHandleDown: Sprite by texture
    val sliderHandleUp: Sprite by texture

    val shadowFadeSize = 48
    val shadowSprite = shadowTexture.getSprite("shadow")

    enum class Theme(val location: Identifier) {
        NORMAL(Identifier("liblib-facade:textures/pastry/light.png")),
        DARK(Identifier("liblib-facade:textures/pastry/dark.png")),
        HIGH_CONTRAST(Identifier("liblib-facade:textures/pastry/contrast.png")),
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
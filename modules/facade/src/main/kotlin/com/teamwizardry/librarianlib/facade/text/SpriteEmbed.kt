package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.mosaic.Sprite
import dev.thecodewarrior.bitfont.typesetting.TypesetGlyph
import java.awt.Color

public class SpriteEmbed(
    override val advance: Int,
    override val ascent: Int,
    override val descent: Int,
    override val bearingX: Int,
    override val bearingY: Int,
    public val sprite: Sprite,
    public val useTint: Boolean
): FacadeTextEmbed() {
    override val width: Int
        get() = sprite.width
    override val height: Int
        get() = sprite.height

    override fun draw(matrix: Matrix3d, typesetGlyph: TypesetGlyph, posX: Int, posY: Int, color: Color) {
        sprite.draw(matrix,
            posX.toFloat() + bearingX, posY.toFloat() + bearingY,
            width.toFloat(), height.toFloat(),
            0,
            if(useTint) color else Color.WHITE
        )
    }
}
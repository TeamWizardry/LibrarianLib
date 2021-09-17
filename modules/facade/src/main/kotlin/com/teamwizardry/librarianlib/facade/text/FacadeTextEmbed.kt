package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.math.Matrix4d
import dev.thecodewarrior.bitfont.typesetting.PositionedGlyph
import dev.thecodewarrior.bitfont.typesetting.TextEmbed
import java.awt.Color

public abstract class FacadeTextEmbed: TextEmbed() {
    public abstract fun draw(matrix: Matrix4d, glyph: PositionedGlyph, posX: Int, posY: Int, color: Color)
}
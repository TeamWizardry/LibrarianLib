package com.teamwizardry.librarianlib.facade.text

import com.teamwizardry.librarianlib.math.Matrix3d
import dev.thecodewarrior.bitfont.typesetting.TextEmbed
import dev.thecodewarrior.bitfont.typesetting.TypesetGlyph
import java.awt.Color

public abstract class FacadeTextEmbed: TextEmbed() {
    public abstract fun draw(matrix: Matrix3d, typesetGlyph: TypesetGlyph, posX: Int, posY: Int, color: Color)
}
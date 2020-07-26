package com.teamwizardry.librarianlib.facade.pastry.components

import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.facade.text.fromMC
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.vec
import dev.thecodewarrior.bitfont.data.Bitfont
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.utils.ExperimentalBitfont

@ExperimentalBitfont
class PastryLabel: TextLayer {
    constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height, "") {
        textMargins = Margins(2.0, 2.0, 2.0, 2.0)
    }
    constructor(posX: Int, posY: Int, text: String): this(posX, posY, 0, 0) {
        this.text = text
        this.fitToText()
    }
    constructor(text: String): this(0, 0, text)
    constructor(posX: Int, posY: Int): this(posX, posY, "")
    constructor(): this(0, 0, "")
}
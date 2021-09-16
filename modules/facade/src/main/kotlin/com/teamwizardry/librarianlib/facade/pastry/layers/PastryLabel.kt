package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.layers.text.TextFit

public class PastryLabel: TextLayer {
    public constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height, "") {
        textMargins = Margins(2.0, 2.0, 2.0, 2.0)
    }

    public constructor(posX: Int, posY: Int, text: String): this(posX, posY, 0, 0) {
        this.text = text
        this.fitToText(TextFit.BOTH)
    }

    public constructor(text: String): this(0, 0, text)
    public constructor(posX: Int, posY: Int): this(posX, posY, "")
    public constructor(): this(0, 0, "")
}
package com.teamwizardry.librarianlib.facade.text

import dev.thecodewarrior.bitfont.utils.Attribute
import java.awt.Color

public object TextAttributes {
    public val color: Attribute<Color> = Attribute.get("color")
    public val obfuscated: Attribute<Boolean> = Attribute.get("obfuscated")
    public val bold: Attribute<Boolean> = Attribute.get("bold")
    public val underline: Attribute<Color> = Attribute.get("underline")
}

@get:JvmSynthetic
public val Attribute.Companion.color: Attribute<Color>
    get() = TextAttributes.color

@get:JvmSynthetic
public val Attribute.Companion.obfuscated: Attribute<Boolean>
    get() = TextAttributes.obfuscated

@get:JvmSynthetic
public val Attribute.Companion.bold: Attribute<Boolean>
    get() = TextAttributes.bold

@get:JvmSynthetic
public val Attribute.Companion.underline: Attribute<Color>
    get() = TextAttributes.underline

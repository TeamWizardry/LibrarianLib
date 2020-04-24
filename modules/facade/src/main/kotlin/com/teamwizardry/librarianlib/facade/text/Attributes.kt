package com.teamwizardry.librarianlib.facade.text

import dev.thecodewarrior.bitfont.utils.Attribute
import java.awt.Color

private val colorAttr = Attribute.get<Color>("color")
val Attribute.Companion.color: Attribute<Color> get() = colorAttr

private val obfuscatedAttr = Attribute.get<Boolean>("obfuscated")
val Attribute.Companion.obfuscated: Attribute<Boolean> get() = obfuscatedAttr

private val boldAttr = Attribute.get<Boolean>("bold")
val Attribute.Companion.bold: Attribute<Boolean> get() = boldAttr

private val underlineAttr = Attribute.get<Color>("underline")
val Attribute.Companion.underline: Attribute<Color> get() = underlineAttr

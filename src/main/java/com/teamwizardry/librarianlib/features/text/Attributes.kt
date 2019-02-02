package com.teamwizardry.librarianlib.features.text

import games.thecodewarrior.bitfont.typesetting.Attribute

private val attrObfuscated = Attribute.get<Boolean>("obfuscated")
val Attribute.Companion.obfuscated: Attribute<Boolean> get() = attrObfuscated

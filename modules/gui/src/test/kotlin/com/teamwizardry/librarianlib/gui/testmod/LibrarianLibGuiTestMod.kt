@file:Suppress("LocalVariableName")

package com.teamwizardry.librarianlib.gui.testmod

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.sprites.ISprite
import com.teamwizardry.librarianlib.sprites.Sprite
import com.teamwizardry.librarianlib.sprites.Texture
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-gui-test")
object LibrarianLibSpritesTestMod: TestMod("gui", "Gui", logger) {
    init {
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Gui Test")

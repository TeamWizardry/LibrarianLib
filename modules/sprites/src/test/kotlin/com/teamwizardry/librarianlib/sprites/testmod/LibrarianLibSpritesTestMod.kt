package com.teamwizardry.librarianlib.sprites.testmod

import com.teamwizardry.librarianlib.LibrarianLibModule
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-sprites-test")
object LibrarianLibSpritesTestMod: LibrarianLibModule("sprites-test", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: Sprites Test")

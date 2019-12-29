package com.teamwizardry.librarianlib.utilities.testmod

import com.teamwizardry.librarianlib.LibrarianLibModule
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-utilities-test")
object LibrarianLibUtilitiesTestMod: LibrarianLibModule("utilities-test", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: Virtual Resources Test")

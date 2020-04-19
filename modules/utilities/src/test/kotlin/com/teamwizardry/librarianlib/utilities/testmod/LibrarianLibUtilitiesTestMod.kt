package com.teamwizardry.librarianlib.utilities.testmod

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-utilities-test")
object LibrarianLibUtilitiesTestMod: TestMod("utilities", "Utilities", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: Utilities Test")

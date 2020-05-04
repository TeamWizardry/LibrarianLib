package com.teamwizardry.librarianlib.prism.testmod

import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-prism-test")
object LibrarianLibPrismTestMod: TestMod("prism", "Prism", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: Prism Test")

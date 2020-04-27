package com.teamwizardry.librarianlib.etcetera.testmod

import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-etcetera-test")
object LibrarianLibEtceteraTestMod: TestMod("etcetera", "Etcetera", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: Etcetera Test")

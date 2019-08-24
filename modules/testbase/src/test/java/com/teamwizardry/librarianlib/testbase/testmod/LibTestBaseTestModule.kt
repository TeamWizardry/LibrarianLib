package com.teamwizardry.librarianlib.testbase.testmod

import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-testbase-test")
class LibTestBaseTestModule: TestMod("testbase", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib/Test Base/Test")

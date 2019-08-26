package com.teamwizardry.librarianlib.xtemplatex.testmod

import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-xtemplatex-test")
class LibXTemplateXTestModule: TestMod("xtemplatex", "UTemplateU" logger)

internal val logger = LogManager.getLogger("LibrarianLib/UTemplateU/Test")

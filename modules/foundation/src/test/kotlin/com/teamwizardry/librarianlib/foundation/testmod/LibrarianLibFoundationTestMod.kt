package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.BaseMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-foundation-test")
object LibrarianLibFoundationTestMod: BaseMod(true) {
}

internal val logger = LogManager.getLogger("LibrarianLib: Foundation Test")

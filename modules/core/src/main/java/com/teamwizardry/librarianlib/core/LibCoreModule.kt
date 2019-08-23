package com.teamwizardry.librarianlib.core

import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-core")
class LibCoreModule : LibrarianLibModule("core", logger)

internal val logger = LogManager.getLogger("LibrarianLib/Core")

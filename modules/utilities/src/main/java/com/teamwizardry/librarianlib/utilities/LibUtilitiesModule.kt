package com.teamwizardry.librarianlib.utilities

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-utilities")
class LibUtilitiesModule : LibrarianLibModule("utilities", logger)

internal val logger = LogManager.getLogger("LibrarianLib/Utilities")

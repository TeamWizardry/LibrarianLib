package com.teamwizardry.librarianlib.utilities

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibUtilitiesModule : LibrarianLibModule("utilities", logger)

internal val logger = LogManager.getLogger("LibrarianLib/Utilities")

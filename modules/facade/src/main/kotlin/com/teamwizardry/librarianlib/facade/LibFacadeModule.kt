package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibFacadeModule : LibrarianLibModule("facade", logger)

internal val logger = LogManager.getLogger("LibrarianLib: Facade")

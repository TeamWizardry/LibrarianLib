package com.teamwizardry.librarianlib.glitter

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibrarianLibGlitterModule : LibrarianLibModule("glitter", logger)
internal val logger = LogManager.getLogger("LibrarianLib: Glitter")

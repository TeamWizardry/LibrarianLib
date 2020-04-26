package com.teamwizardry.librarianlib.mirage

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibMirageModule : LibrarianLibModule("mirage", logger)

internal val logger = LogManager.getLogger("LibrarianLib: Mirage")

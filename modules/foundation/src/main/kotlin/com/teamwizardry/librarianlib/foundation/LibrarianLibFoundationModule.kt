package com.teamwizardry.librarianlib.foundation

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibrarianLibFoundationModule : LibrarianLibModule("foundation", logger)

@get:JvmSynthetic
internal val logger = LogManager.getLogger("LibrarianLib: Foundation")

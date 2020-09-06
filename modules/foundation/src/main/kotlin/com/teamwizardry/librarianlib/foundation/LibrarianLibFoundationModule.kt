package com.teamwizardry.librarianlib.foundation

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

internal object LibrarianLibFoundationModule : LibrarianLibModule("foundation", "Foundation")

internal val logger = LibrarianLibFoundationModule.makeLogger(null)

package com.teamwizardry.librarianlib.gui

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibGuiModule : LibrarianLibModule("gui", logger)

internal val logger = LogManager.getLogger("LibrarianLib: Gui")

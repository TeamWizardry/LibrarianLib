package com.teamwizardry.librarianlib.mirage

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

internal object LibrarianLibMirageModule : LibrarianLibModule("mirage", "Mirage")

internal val logger = LibrarianLibMirageModule.makeLogger(null)

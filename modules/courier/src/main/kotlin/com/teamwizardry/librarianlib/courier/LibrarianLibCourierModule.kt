package com.teamwizardry.librarianlib.courier

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

internal object LibrarianLibCourierModule : LibrarianLibModule("courier", "Courier") {
}

internal val logger = LibrarianLibCourierModule.makeLogger(null)

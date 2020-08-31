package com.teamwizardry.librarianlib.prism

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibrarianLibPrismModule : LibrarianLibModule("prism", "Prism") {
}

internal val logger = LibrarianLibPrismModule.makeLogger(null)

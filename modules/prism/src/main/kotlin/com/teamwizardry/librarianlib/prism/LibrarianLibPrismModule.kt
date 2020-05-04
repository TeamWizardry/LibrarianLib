package com.teamwizardry.librarianlib.prism

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibrarianLibPrismModule : LibrarianLibModule("prism", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: Prism")

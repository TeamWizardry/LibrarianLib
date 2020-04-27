package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibEtceteraModule : LibrarianLibModule("etcetera", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: Etcetera")

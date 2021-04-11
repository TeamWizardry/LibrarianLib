package com.teamwizardry.librarianlib.${lowername}

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

internal object LibrarianLib${PascalName}Module : LibrarianLibModule("${lowername}", "${humanName}")

internal val logger = LibrarianLib${PascalName}Module.makeLogger(null)

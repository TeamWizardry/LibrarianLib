package com.teamwizardry.librarianlib.virtualresources

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibVirtualResourcesModule : LibrarianLibModule("virtualresources", logger)

internal val logger = LogManager.getLogger("LibrarianLib: Virtual Resources")

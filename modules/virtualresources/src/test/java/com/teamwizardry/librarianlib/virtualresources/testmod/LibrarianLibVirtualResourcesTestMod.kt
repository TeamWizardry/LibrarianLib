package com.teamwizardry.librarianlib.virtualresources.testmod

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

class LibrarianLibVirtualResourcesTestMod: LibrarianLibModule("virtualresources-test", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib/Virtual Resources/Test")

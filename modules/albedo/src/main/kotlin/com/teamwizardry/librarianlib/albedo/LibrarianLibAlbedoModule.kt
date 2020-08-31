package com.teamwizardry.librarianlib.albedo

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibrarianLibAlbedoModule : LibrarianLibModule("albedo", "Albedo") {
}

internal val logger = LibrarianLibAlbedoModule.makeLogger(null)

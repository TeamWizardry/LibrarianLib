package com.teamwizardry.librarianlib.particles

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object LibParticlesModule : LibrarianLibModule("particles", logger)
internal val logger = LogManager.getLogger("LibrarianLib/Particles")

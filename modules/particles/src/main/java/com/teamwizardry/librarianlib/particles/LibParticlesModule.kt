package com.teamwizardry.librarianlib.particles

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-particles")
class LibParticlesModule : LibrarianLibModule("particles", logger)
internal val logger = LogManager.getLogger("LibrarianLib/Particles")

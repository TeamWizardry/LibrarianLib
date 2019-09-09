package com.teamwizardry.librarianlib.virtualresources

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import net.minecraft.resources.FallbackResourceManager
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-virtualresources")
class LibVirtualResourcesModule : LibrarianLibModule("virtualresources", logger)

internal val logger = LogManager.getLogger("LibrarianLib/Virtual Resources")

package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SidedRunnable
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

object LibrarianLibEtceteraModule : LibrarianLibModule("etcetera", "Etcetera")

internal val logger = LibrarianLibEtceteraModule.makeLogger(null)

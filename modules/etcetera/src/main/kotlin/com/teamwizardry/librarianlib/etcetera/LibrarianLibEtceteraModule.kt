package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SidedRunnable
import org.apache.logging.log4j.LogManager

object LibrarianLibEtceteraModule : LibrarianLibModule("etcetera", logger) {
    init {
        SidedRunnable.client {
            if (!Client.minecraft.framebuffer.isStencilEnabled)
                Client.minecraft.framebuffer.enableStencil()
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Etcetera")

package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.SidedRunnable
import com.teamwizardry.librarianlib.etcetera.StencilUtil
import org.apache.logging.log4j.LogManager

object LibrarianLibFacadeModule: LibrarianLibModule("facade", logger) {
    init {
        SidedRunnable.client {
            StencilUtil.enableStencilBuffer()
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Facade")

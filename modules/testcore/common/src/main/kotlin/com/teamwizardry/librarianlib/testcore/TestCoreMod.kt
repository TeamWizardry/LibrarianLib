package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.core.util.ModLogManager


public object TestCoreMod {
    public const val MODID: String = "liblib-testcore"
    public val logManager: ModLogManager = ModLogManager(MODID, "LibrarianLib: Test Core")
}

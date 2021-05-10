package com.teamwizardry.librarianlib

import com.teamwizardry.librarianlib.core.util.ModLogManager
import net.fabricmc.api.ModInitializer

public object LibrarianLib : ModInitializer {
    public val logManager: ModLogManager = ModLogManager("librarianlib", "LibrarianLib")

    override fun onInitialize() {
    }
}
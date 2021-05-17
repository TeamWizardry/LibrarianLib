package com.teamwizardry.librarianlib.facade.test

import net.minecraft.client.gui.screen.Screen

data class FacadeTestGroup(override val name: String, val tests: List<FacadeTestEntry>): FacadeTestEntry {
    override fun create(): Screen {
        return TestListScreen(name, tests)
    }
}
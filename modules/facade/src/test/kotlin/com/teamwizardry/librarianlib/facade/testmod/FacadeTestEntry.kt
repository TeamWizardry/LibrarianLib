package com.teamwizardry.librarianlib.facade.testmod

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen

interface FacadeTestEntry {
    val name: String
    @Environment(EnvType.CLIENT)
    fun create(): Screen
}
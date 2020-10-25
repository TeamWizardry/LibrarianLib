package com.teamwizardry.librarianlib.facade.testmod

import net.minecraft.client.gui.screen.Screen

interface FacadeTestEntry {
    val name: String
    fun create(): Screen
}
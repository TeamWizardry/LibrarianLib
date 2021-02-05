package com.teamwizardry.librarianlib.facade.testmod

import net.minecraft.client.gui.screen.Screen
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

interface FacadeTestEntry {
    val name: String
    @OnlyIn(Dist.CLIENT)
    fun create(): Screen
}
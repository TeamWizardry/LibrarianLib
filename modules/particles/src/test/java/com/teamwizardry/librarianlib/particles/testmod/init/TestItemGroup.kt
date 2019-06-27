package com.teamwizardry.librarianlib.particles.testmod.init

import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object TestItemGroup: ItemGroup("liblibtest.particles") {
    override fun createIcon(): ItemStack {
        return ItemStack(Items.IRON_INGOT)
    }
}
package com.teamwizardry.librarianlib.glitter.testmod.init

import com.teamwizardry.librarianlib.glitter.testmod.item.ParticleSpawnerItem
import net.minecraftforge.registries.ForgeRegistries

object TestItems {
    val items = listOf(
        "static",
        "physics",
        "flood"
    ).associateWith { ParticleSpawnerItem(it) }

    fun register() {
        items.forEach {
            ForgeRegistries.ITEMS.register(it.value)
        }
    }
}
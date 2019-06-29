package com.teamwizardry.librarianlib.particles.testmod.init

import com.teamwizardry.librarianlib.particles.testmod.item.ParticleSpawnerItem
import net.minecraftforge.registries.ForgeRegistries

object TestItems {
    val items = listOf(
        "static",
        "physics"
    ).associateWith { ParticleSpawnerItem(it) }

    fun register() {
        items.forEach {
            ForgeRegistries.ITEMS.register(it.value)
        }
    }
}
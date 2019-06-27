package com.teamwizardry.librarianlib.particles.testmod.init

import com.teamwizardry.librarianlib.particles.testmod.item.ParticleSpawnerItem
import net.minecraftforge.registries.ForgeRegistries

object TestItems {
    val items = listOf(
        "static"
    ).associateWith { ParticleSpawnerItem(it) }

    fun register() {
        items.forEach {
            ForgeRegistries.ITEMS.register(it.value)
        }
    }
}
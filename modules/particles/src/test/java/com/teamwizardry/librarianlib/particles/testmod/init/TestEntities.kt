package com.teamwizardry.librarianlib.particles.testmod.init

import com.teamwizardry.librarianlib.particles.testmod.entity.ParticleSpawnerEntity
import com.teamwizardry.librarianlib.particles.testmod.modid
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.AreaEffectCloudEntity
import net.minecraft.entity.EntityType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries

object TestEntities {
    val spawner = EntityType.Builder.create<ParticleSpawnerEntity>({ _, world ->
        ParticleSpawnerEntity(world)
    }, EntityClassification.MISC)
        .setCustomClientFactory { _, world ->
            ParticleSpawnerEntity(world)
        }
        .size(0.5f, 0.5f).build("particle_spawner")
        .setRegistryName(modid, "particle_spawner")

    fun register() {
        ForgeRegistries.ENTITIES.register(spawner)
    }
}
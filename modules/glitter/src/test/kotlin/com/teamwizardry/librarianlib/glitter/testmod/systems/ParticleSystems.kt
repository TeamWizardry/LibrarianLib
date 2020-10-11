package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.glitter.ParticleSystemManager
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

object ParticleSystems {
    val systems: List<TestSystem> = listOf(
        StaticSystem,
        PhysicsSystem,
        FloodSystem,
        PerfectBouncySystem,
        ForwardFacingSystem,
        SpriteSheetSystem,
        DepthSortSystem,
        IgnoreParticleSettingSystem,
        ShowOnMinimalSystem,
        SpawnCountAdjustmentSystem,
        PartialTickLerpSystem
    )
    private val systemMap = systems.associateBy { it.id }

    init {
        systems.forEach {
            it.addToGame()
        }
    }

    fun spawn(name: String, player: Entity) {
        systemMap[name]?.spawn(player)
    }
}
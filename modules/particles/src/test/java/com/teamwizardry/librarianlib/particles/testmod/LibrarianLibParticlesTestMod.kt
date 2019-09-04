package com.teamwizardry.librarianlib.particles.testmod

import com.teamwizardry.librarianlib.particles.testmod.entity.ParticleSpawnerEntity
import com.teamwizardry.librarianlib.particles.testmod.entity.ParticleSpawnerEntityRenderer
import com.teamwizardry.librarianlib.particles.testmod.init.TestEntities
import com.teamwizardry.librarianlib.particles.testmod.init.TestItems
import com.teamwizardry.librarianlib.particles.testmod.systems.ParticleSystems
import com.teamwizardry.librarianlib.particles.testmod.systems.SystemNames
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestItemConfig
import net.minecraft.block.Block
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.LogManager

internal const val modid: String = "librarianlib-particles-test"

@Mod(modid)
class LibrarianLibParticlesTestMod: TestMod("particles", "Particle System", logger) {
    init {
        SystemNames.systems.forEach { system ->
            val entity =
            +TestItem(TestItemConfig("spawn_${system.id}", system.name) {
                description = system.description
                server {
                    rightClick {
                        if(player.isSneaking) {
                            val eye = player.getEyePosition(0f)
                            val spawner = ParticleSpawnerEntity(world)
                            spawner.system = system.id
                            spawner.posX = eye.x
                            spawner.posY = eye.y - spawner.eyeHeight
                            spawner.posZ = eye.z
                            spawner.rotationPitch = player.rotationPitch
                            spawner.rotationYaw = player.rotationYaw
                            world.addEntity(spawner)
                        }
                    }
                }

                client {
                    rightClickHold {
                        ParticleSystems.spawn(system.id, player)
                    }
                }
            })
        }
    }

    override fun clientSetup(event: FMLClientSetupEvent) {
        super.clientSetup(event)
        RenderingRegistry.registerEntityRenderingHandler(ParticleSpawnerEntity::class.java) { ParticleSpawnerEntityRenderer(it) }
    }

    override fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
        super.registerItems(itemRegistryEvent)
    }

    override fun registerEntities(entityRegistryEvent: RegistryEvent.Register<EntityType<*>>) {
        super.registerEntities(entityRegistryEvent)
        TestEntities.register()
    }
}

internal val logger = LogManager.getLogger("LibrarianLib/Particles/Test")

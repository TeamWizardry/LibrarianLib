package com.teamwizardry.librarianlib.particles.testmod

import com.teamwizardry.librarianlib.particles.testmod.entity.ParticleSpawnerEntity
import com.teamwizardry.librarianlib.particles.testmod.entity.ParticleSpawnerEntityRenderer
import com.teamwizardry.librarianlib.particles.testmod.init.TestEntities
import com.teamwizardry.librarianlib.particles.testmod.init.TestItems
import com.teamwizardry.librarianlib.particles.testmod.systems.ParticleSystems
import com.teamwizardry.librarianlib.particles.testmod.systems.SystemNames
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestEntityConfig
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
            +TestEntityConfig(system.id, system.name) {
                description = system.description

                tick.client {
                    ParticleSystems.spawn(system.id, target)
                }

                spawnerItem.config {
                    rightClick.clear()

                    rightClick.server {
                        sneaking {
                            spawn(player)
                        }
                    }
                    rightClickHold.client {
                        notSneaking {
                            ParticleSystems.spawn(system.id, player)
                        }
                    }
                }
            }
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

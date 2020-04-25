package com.teamwizardry.librarianlib.glitter.testmod

import com.teamwizardry.librarianlib.glitter.testmod.init.TestEntities
import com.teamwizardry.librarianlib.glitter.testmod.systems.ParticleSystems
import com.teamwizardry.librarianlib.glitter.testmod.systems.SystemNames
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

internal const val modid: String = "librarianlib-glitter-test"

@Mod("librarianlib-glitter-test")
object LibrarianLibGlitterTestMod: TestMod("glitter", "Glitter", logger) {
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

    override fun registerEntities(entityRegistryEvent: RegistryEvent.Register<EntityType<*>>) {
        super.registerEntities(entityRegistryEvent)
        TestEntities.register()
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Particles Test")

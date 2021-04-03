package com.teamwizardry.librarianlib.glitter.testmod

import com.teamwizardry.librarianlib.glitter.LibrarianLibGlitterModule
import com.teamwizardry.librarianlib.glitter.testmod.init.TestEntities
import com.teamwizardry.librarianlib.glitter.testmod.systems.ParticleSystems
import com.teamwizardry.librarianlib.glitter.testmod.systems.SystemNames
import com.teamwizardry.librarianlib.testcore.TestMod
import net.minecraft.entity.EntityType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod

internal const val modid: String = "ll-glitter-test"

@Mod("ll-glitter-test")
object LibrarianLibGlitterTestMod: TestMod(LibrarianLibGlitterModule) {
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

    override fun registerEntities(e: RegistryEvent.Register<EntityType<*>>) {
        super.registerEntities(e)
        TestEntities.register()
    }
}

internal val logger = LibrarianLibGlitterTestMod.makeLogger(null)

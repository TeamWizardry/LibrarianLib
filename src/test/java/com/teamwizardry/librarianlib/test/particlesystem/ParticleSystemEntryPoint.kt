package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

object ParticleSystemEntryPoint: TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {
    }

    override fun init(event: FMLInitializationEvent) {
    }

    override fun postInit(event: FMLPostInitializationEvent) {
    }

    val item = ItemParticleSystemTest()
    val fountain = BlockParticleTest()
}

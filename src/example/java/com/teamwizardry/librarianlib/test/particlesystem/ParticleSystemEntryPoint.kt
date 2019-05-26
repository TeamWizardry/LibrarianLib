package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.base.block.BlockMod
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

object ParticleSystemEntryPoint : TestEntryPoint {
    var item: ItemMod? = null
    var fountain: BlockMod? = null
    var particleMaker: ItemParticleMaker? = null

    override fun preInit(event: FMLPreInitializationEvent) {
        item = ItemParticleSystemTest()
        fountain = BlockParticleTest()

        particleMaker = ItemParticleMaker()
    }

    override fun init(event: FMLInitializationEvent) {
    }

    override fun postInit(event: FMLPostInitializationEvent) {
    }
}

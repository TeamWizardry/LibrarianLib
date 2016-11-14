package com.teamwizardry.librarianlib.test.fx

import com.teamwizardry.librarianlib.client.util.CustomBlockMapSprites
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import com.teamwizardry.librarianlib.test.testcore.TestMod
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
object FXEntryPoint : TestEntryPoint {
    override fun init(event: FMLInitializationEvent) {
        FXItemRegister
        CustomBlockMapSprites.register(ResourceLocation(TestMod.MODID, "particles/glow"))
    }

    override fun postInit(event: FMLPostInitializationEvent) {

    }

    override fun preInit(event: FMLPreInitializationEvent) {

    }

}

object FXItemRegister {
    val basic_particles = ItemBasicParticles()
}

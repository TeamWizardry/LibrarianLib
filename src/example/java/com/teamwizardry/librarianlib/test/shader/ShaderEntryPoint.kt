package com.teamwizardry.librarianlib.test.shader

import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
object ShaderEntryPoint : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {
        ShaderItems
    }

    override fun init(event: FMLInitializationEvent) {

    }

    override fun postInit(event: FMLPostInitializationEvent) {

    }
}

object ShaderItems {
    val shaderitem = ItemShaderOpener()
}

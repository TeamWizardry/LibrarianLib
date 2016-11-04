package com.teamwizardry.librarianlib.test.testcore

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
interface TestEntryPoint {
    fun preInit(event: FMLPreInitializationEvent)
    fun init(event: FMLInitializationEvent)
    fun postInit(event: FMLPostInitializationEvent)
}

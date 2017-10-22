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

/**
 * So git diffs in the entry point list are sane. Because we can't do trailing commas we need some null entry point at
 * the end
 */
object EndEntryPoint : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) { }
    override fun init(event: FMLInitializationEvent) { }
    override fun postInit(event: FMLPostInitializationEvent) { }
}

package com.teamwizardry.librarianlib.test.testcore

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
open class LibTestCommonProxy {
    fun pre(e: FMLPreInitializationEvent) {}
    fun init(e: FMLInitializationEvent) {}
    fun post(e: FMLPostInitializationEvent) {}
}

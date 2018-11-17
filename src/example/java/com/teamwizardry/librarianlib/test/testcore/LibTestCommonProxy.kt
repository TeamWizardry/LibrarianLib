package com.teamwizardry.librarianlib.test.testcore

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
open class LibTestCommonProxy {
    open fun pre(e: FMLPreInitializationEvent) {
        TestMod.Companion.Tab
    }

    open fun init(e: FMLInitializationEvent) {
    }

    open fun post(e: FMLPostInitializationEvent) {
    }
}

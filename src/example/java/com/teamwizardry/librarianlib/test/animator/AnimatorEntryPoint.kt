package com.teamwizardry.librarianlib.test.animator

import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
object AnimatorEntryPoint : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {
        AnimatorItems
        //  PearlRadialUIRenderer.INSTANCE.javaClass
    }

    override fun init(event: FMLInitializationEvent) {

    }

    override fun postInit(event: FMLPostInitializationEvent) {

    }
}

object AnimatorItems {
    val animatoritem = ItemAnimator()
}

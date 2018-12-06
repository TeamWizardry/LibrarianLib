package com.teamwizardry.librarianlib.test.worlddata

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.worlddata.WorldData
import com.teamwizardry.librarianlib.features.worlddata.WorldDataRegistry
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import com.teamwizardry.librarianlib.test.testcore.TestMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * TODO: Document file ChunkDataEntryPoint
 *
 * Created by TheCodeWarrior
 */
@Mod.EventBusSubscriber(modid = TestMod.MODID)
object WorldDataEntryPoint : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {
        WorldDataRegistry.register("librarianlibtest:jumpdata".toRl(), TestWorldData::class.java, ::TestWorldData, { true })
    }

    override fun init(event: FMLInitializationEvent) {
    }

    override fun postInit(event: FMLPostInitializationEvent) {
    }

    val item = ItemWorldData()

    @SubscribeEvent
    @JvmStatic
    fun jump(e: LivingEvent.LivingJumpEvent) {
        if(e.entity is EntityPlayer && !e.entity.world.isRemote) {
            val data = WorldData.get(e.entity.world, TestWorldData::class.java)
            if(data != null) {
                data.jumps++
                data.markDirty()
            }
        }
    }
}

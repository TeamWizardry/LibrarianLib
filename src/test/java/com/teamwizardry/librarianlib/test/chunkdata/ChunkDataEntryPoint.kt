package com.teamwizardry.librarianlib.test.chunkdata

import com.teamwizardry.librarianlib.features.chunkdata.ChunkDataRegistry
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * TODO: Document file ChunkDataEntryPoint
 *
 * Created by TheCodeWarrior
 */
object ChunkDataEntryPoint : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {
        ChunkDataRegistry.register("librarianlibtest:clickdata".toRl(), TestChunkData::class.java, ::TestChunkData, { true })
    }

    override fun init(event: FMLInitializationEvent) {
    }

    override fun postInit(event: FMLPostInitializationEvent) {
    }

    val item = ItemChunkData()
}

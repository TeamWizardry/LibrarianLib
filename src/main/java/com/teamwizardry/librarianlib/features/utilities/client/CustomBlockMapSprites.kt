package com.teamwizardry.librarianlib.features.utilities.client

import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Created by TheCodeWarrior
 */
object CustomBlockMapSprites {
    private val customBlockMapLocations = mutableSetOf<ResourceLocation>()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun register(loc: ResourceLocation) {
        customBlockMapLocations.add(loc)
    }

    @SubscribeEvent
    fun stitch(event: TextureStitchEvent.Pre) {
        customBlockMapLocations.forEach { event.map.registerSprite(it) }
    }
}

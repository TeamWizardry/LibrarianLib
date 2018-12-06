package com.teamwizardry.librarianlib.features.utilities.client

import com.teamwizardry.librarianlib.core.LibrarianLib
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by TheCodeWarrior
 */
@Mod.EventBusSubscriber(value = [Side.CLIENT], modid = LibrarianLib.MODID)
object CustomBlockMapSprites {
    private val customBlockMapLocations = mutableSetOf<ResourceLocation>()

    fun register(loc: ResourceLocation) {
        customBlockMapLocations.add(loc)
    }

    @JvmStatic
    @SubscribeEvent
    fun stitch(event: TextureStitchEvent.Pre) {
        customBlockMapLocations.forEach { event.map.registerSprite(it) }
    }
}

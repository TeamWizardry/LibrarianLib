package com.teamwizardry.librarianlib.client.sprite

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.data.IMetadataSection
import net.minecraft.client.resources.data.MetadataSerializer
import net.minecraftforge.fml.relauncher.ReflectionHelper

class SpritesMetadataSection(var width: Int, var height: Int, var definitions: List<SpritesMetadataSection.SpriteDefinition>) : IMetadataSection {

    class SpriteDefinition(var name: String, var u: Int, var v: Int, var w: Int, var h: Int, var frames: IntArray, var offsetU: Int, var offsetV: Int)


    companion object {
        public var registered = false;
    }
}

package com.teamwizardry.librarianlib.client.sprite

import net.minecraft.client.resources.data.IMetadataSection
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.fml.relauncher.Side

@SideOnly(Side.CLIENT)
class SpritesMetadataSection(var width: Int, var height: Int, var definitions: List<SpritesMetadataSection.SpriteDefinition>) : IMetadataSection {
    class SpriteDefinition(var name: String, var u: Int, var v: Int, var w: Int, var h: Int, var frames: IntArray, var offsetU: Int, var offsetV: Int)

    companion object {
        var registered = false;
    }
}

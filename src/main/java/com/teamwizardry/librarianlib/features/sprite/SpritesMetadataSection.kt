package com.teamwizardry.librarianlib.features.sprite

import net.minecraft.client.resources.data.IMetadataSection
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class SpritesMetadataSection(var width: Int, var height: Int, var definitions: List<SpriteDefinition>) : IMetadataSection {
    companion object {
        var registered = false
    }
}


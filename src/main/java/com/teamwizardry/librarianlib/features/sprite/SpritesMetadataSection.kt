package com.teamwizardry.librarianlib.features.sprite

import net.minecraft.client.resources.data.IMetadataSection
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Arrays

@SideOnly(Side.CLIENT)
class SpritesMetadataSection(var width: Int, var height: Int, var sprites: List<SpriteDefinition>, var colors: List<ColorDefinition>) : IMetadataSection {
    companion object {
        var registered = false
    }
}

class SpriteDefinition(
    var name: String,
    var u: Int, var v: Int, var w: Int, var h: Int,
    var frames: IntArray, var offsetU: Int, var offsetV: Int,
    var minUCap: Int, var minVCap: Int, var maxUCap: Int, var maxVCap: Int,
    var hardScaleU: Boolean, var hardScaleV: Boolean) {
    constructor(name: String) : this(
        name,
        0, 0, 0, 0,
        intArrayOf(0), 0, 0,
        0, 0, 0, 0,
        false, false
    )

    override fun toString(): String {
        return "SpriteDefinition(" +
            "name='$name', uv=($u,$v), wh=($w,$h), frames=${Arrays.toString(frames)}, offsetUV=($offsetU,$offsetV), " +
            "cap=($minUCap,$minVCap,$maxUCap,$maxVCap), hardScaleUV=($hardScaleU,$hardScaleV)" +
            ")"
    }
}

class ColorDefinition(var name: String, var u: Int, var v: Int) {
    constructor(name: String) : this(name, -1, -1)
}

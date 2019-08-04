package com.teamwizardry.librarianlib.sprite

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

class SpriteDefinition(var name: String) {

    var u: Int = 0
    var v: Int = 0
    var w: Int = 0
    var h: Int = 0

    var frames: IntArray = IntArray(0)
    var offsetU: Int = 0
    var offsetV: Int = 0

    var minUCap: Int = 0
    var minVCap: Int = 0
    var maxUCap: Int = 0
    var maxVCap: Int = 0

    var pinTop: Boolean = true
    var pinRight: Boolean = true
    var pinBottom: Boolean = true
    var pinLeft: Boolean = true

    override fun toString(): String {
        return "SpriteDefinition(" +
            "name='$name', uv=($u,$v), wh=($w,$h), frames=${Arrays.toString(frames)}, offsetUV=($offsetU,$offsetV), " +
            "cap=($minUCap,$minVCap,$maxUCap,$maxVCap), pinned=($pinTop,$pinRight,$pinBottom,$pinLeft)" +
            ")"
    }
}

class ColorDefinition(var name: String, var u: Int, var v: Int) {
    constructor(name: String) : this(name, -1, -1)
}

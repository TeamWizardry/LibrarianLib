package com.teamwizardry.librarianlib.sprites

import java.util.Arrays

internal class SpritesheetJson(var width: Int, var height: Int, var sprites: List<SpriteJson>, var colors: List<ColorJson>) {
    companion object {
        val SERIALIZER = SpritesMetadataSectionSerializer()
    }
}

internal class SpriteJson(var name: String) {

    var u: Int = 0
    var v: Int = 0
    var w: Int = 0
    var h: Int = 0

    var frames: IntArray = intArrayOf(0)
    var offsetU: Int = 0
    var offsetV: Int = 0

    var minUCap: Int = 0
    var minVCap: Int = 0
    var maxUCap: Int = 0
    var maxVCap: Int = 0

    var pinLeft: Boolean = true
    var pinTop: Boolean = true
    var pinRight: Boolean = true
    var pinBottom: Boolean = true
}

internal class ColorJson(var name: String, var u: Int, var v: Int)

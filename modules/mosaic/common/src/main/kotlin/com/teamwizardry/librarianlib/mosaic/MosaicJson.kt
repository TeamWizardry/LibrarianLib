package com.teamwizardry.librarianlib.mosaic

internal class MosaicJson(
    var width: Int, var height: Int,
    var blur: Boolean, var mipmap: Boolean,
    var sprites: List<SpriteJson>, var colors: List<ColorJson>
)

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

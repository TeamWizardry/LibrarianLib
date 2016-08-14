package com.teamwizardry.librarianlib.structure

import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos

import java.util.ArrayList

class StructureMatchResult(var posOffset: BlockPos, var rotation: Rotation, var structure: Structure) {

    var allErrors: List<BlockPos> = ArrayList()
    var airErrors: List<BlockPos> = ArrayList()
    var nonAirErrors: List<BlockPos> = ArrayList()
    var propertyErrors: List<BlockPos> = ArrayList()
    var matches: List<BlockPos> = ArrayList()
}

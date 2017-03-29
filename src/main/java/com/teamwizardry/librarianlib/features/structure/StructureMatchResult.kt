package com.teamwizardry.librarianlib.features.structure

import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos

class StructureMatchResult(var posOffset: BlockPos, var rotation: Rotation, var structure: Structure) {

    var allErrors: MutableList<BlockPos> = mutableListOf()
    var airErrors: MutableList<BlockPos> = mutableListOf()
    var nonAirErrors: MutableList<BlockPos> = mutableListOf()
    var propertyErrors: MutableList<BlockPos> = mutableListOf()
    var matches: MutableList<BlockPos> = mutableListOf()
}

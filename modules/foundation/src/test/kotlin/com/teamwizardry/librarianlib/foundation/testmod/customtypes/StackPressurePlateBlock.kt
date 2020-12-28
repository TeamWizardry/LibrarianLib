package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.foundation.block.BaseAnalogPressurePlateBlock
import com.teamwizardry.librarianlib.foundation.block.FoundationBlockProperties
import net.minecraft.entity.item.ItemEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class StackPressurePlateBlock(properties: FoundationBlockProperties, textureName: String) :
    BaseAnalogPressurePlateBlock(properties, textureName) {
    override fun computeRedstoneStrength(world: World, pos: BlockPos): Int {
        return this.getEntitiesOnPressurePlate(world, pos, true)
            .filterIsInstance<ItemEntity>()
            .sumBy { it.item.count } % 16
    }
}
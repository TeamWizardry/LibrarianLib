package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.DyeColor
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.ToolType
import java.util.function.Supplier

class DefaultProperties {
    private val configs = mutableListOf<(BlockSpec) -> Unit>()

    fun apply(spec: BlockSpec) {
        configs.forEach {
            it(spec)
        }
    }

    fun propertiesFrom(other: DefaultProperties): DefaultProperties = build {
        this.configs.addAll(other.configs)
    }

    fun material(material: Material): DefaultProperties = build { it.material(material) }
    fun mapColor(color: MaterialColor): DefaultProperties = build { it.mapColor(color) }
    fun mapColor(color: DyeColor): DefaultProperties = build { it.mapColor(color) }
    fun doesNotBlockMovement(): DefaultProperties = build { it.doesNotBlockMovement() }
    fun notSolid(): DefaultProperties = build { it.notSolid() }
    fun slipperiness(slipperiness: Float): DefaultProperties = build { it.slipperiness(slipperiness) }
    fun speedFactor(factor: Float): DefaultProperties = build { it.speedFactor(factor) }
    fun jumpFactor(factor: Float): DefaultProperties = build { it.jumpFactor(factor) }
    fun sound(soundType: SoundType): DefaultProperties = build { it.sound(soundType) }
    fun lightValue(lightValue: Int): DefaultProperties = build { it.lightValue(lightValue) }
    fun hardnessAndResistance(hardness: Float, resistance: Float): DefaultProperties = build { it.hardnessAndResistance(hardness, resistance) }
    fun hardnessAndResistance(hardnessAndResistance: Float): DefaultProperties = build { it.hardnessAndResistance(hardnessAndResistance) }
    fun tickRandomly(): DefaultProperties = build { it.tickRandomly() }
    fun variableOpacity(): DefaultProperties = build { it.variableOpacity() }
    fun harvestLevel(harvestLevel: Int): DefaultProperties = build { it.harvestLevel(harvestLevel) }
    fun harvestTool(harvestTool: ToolType): DefaultProperties = build { it.harvestTool(harvestTool) }
    fun noDrops(): DefaultProperties = build { it.noDrops() }
    fun lootFrom(other: Block): DefaultProperties = build { it.lootFrom(other) }

    private fun build(block: (BlockSpec) -> Unit): DefaultProperties {
        configs.add(block)
        return this
    }
}
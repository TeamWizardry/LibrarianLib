package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.DyeColor
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.ToolType
import java.util.function.Supplier

public class DefaultProperties {
    private val configs = mutableListOf<(BlockSpec) -> Unit>()

    public fun apply(spec: BlockSpec) {
        configs.forEach {
            it(spec)
        }
    }

    public fun propertiesFrom(other: DefaultProperties): DefaultProperties = build {
        this.configs.addAll(other.configs)
    }

    public fun material(material: Material): DefaultProperties = build { it.material(material) }
    public fun mapColor(color: MaterialColor): DefaultProperties = build { it.mapColor(color) }
    public fun mapColor(color: DyeColor): DefaultProperties = build { it.mapColor(color) }
    public fun doesNotBlockMovement(): DefaultProperties = build { it.doesNotBlockMovement() }
    public fun notSolid(): DefaultProperties = build { it.notSolid() }
    public fun slipperiness(slipperiness: Float): DefaultProperties = build { it.slipperiness(slipperiness) }
    public fun speedFactor(factor: Float): DefaultProperties = build { it.speedFactor(factor) }
    public fun jumpFactor(factor: Float): DefaultProperties = build { it.jumpFactor(factor) }
    public fun sound(soundType: SoundType): DefaultProperties = build { it.sound(soundType) }
    public fun lightValue(lightValue: Int): DefaultProperties = build { it.lightValue(lightValue) }
    public fun hardnessAndResistance(hardness: Float, resistance: Float): DefaultProperties = build { it.hardnessAndResistance(hardness, resistance) }
    public fun hardnessAndResistance(hardnessAndResistance: Float): DefaultProperties = build { it.hardnessAndResistance(hardnessAndResistance) }
    public fun tickRandomly(): DefaultProperties = build { it.tickRandomly() }
    public fun variableOpacity(): DefaultProperties = build { it.variableOpacity() }
    public fun harvestLevel(harvestLevel: Int): DefaultProperties = build { it.harvestLevel(harvestLevel) }
    public fun harvestTool(harvestTool: ToolType): DefaultProperties = build { it.harvestTool(harvestTool) }
    public fun noDrops(): DefaultProperties = build { it.noDrops() }
    public fun lootFrom(other: Block): DefaultProperties = build { it.lootFrom(other) }

    private fun build(block: (BlockSpec) -> Unit): DefaultProperties {
        configs.add(block)
        return this
    }
}
package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.foundation.block.BaseLeavesBlock
import com.teamwizardry.librarianlib.foundation.block.BaseLogBlock
import com.teamwizardry.librarianlib.foundation.block.BaseRotatedPillarBlock
import com.teamwizardry.librarianlib.foundation.block.FoundationBlockProperties
import com.teamwizardry.librarianlib.foundation.util.TagWrappers
import net.minecraft.block.Block
import net.minecraft.block.RotatedPillarBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.Item
import net.minecraft.tags.BlockTags
import net.minecraft.tags.Tag

/**
 * All the basic blocks for a type of wood.
 *
 * The properties common to the logs (normal and stripped, all-stripped and all-bark) can be configured using
 * [logProperties], and the properties common to all the planks can be configured through [plankProperties].
 * Properties for specific blocks can be configured directly after retrieving their [BlockSpec].
 *
 * @param woodName The id of the wood. e.g. "spruce", "wisdom_wood", etc. This will be turned into names like
 * "xxx_planks", "stripped_xxx_log", etc.
 * @param woodColor The color of the planks and the ends of logs
 * @param barkColor The color of the sides of logs
 * @param hardnessMultiplier A multiplier for the default block hardness
 * @param resistanceMultiplier A multiplier for the default block resistance
 */
public class WoodBlockCollection @JvmOverloads constructor(
    private val registrationManager: RegistrationManager,
    private val woodName: String,
    private val woodColor: MaterialColor,
    private val barkColor: MaterialColor,
    private val hardnessMultiplier: Float = 1f,
    private val resistanceMultiplier: Float = 1f
) {

    private val planksCollection: BuildingBlockCollection = BuildingBlockCollection(woodName + "_planks", woodName)

    public val plankProperties: FoundationBlockProperties = planksCollection.blockProperties

    /**
     * Custom block properties to apply to the logs in this collection. Block spec instances are lazy, so this
     * has to be configured before accessing any of them for changes to apply.
     *
     * Individual block spec properties can be adjusted after they're initialized.
     */
    public val logProperties: FoundationBlockProperties = FoundationBlockProperties()

    /**
     * What layer the blocks should be rendered in
     */
    public var renderLayer: RenderLayerSpec = RenderLayerSpec.SOLID
        set(value) {
            field = value
            planksCollection.renderLayer = value
        }

    /**
     * The item group to put the blocks in
     */
    public var itemGroup: ItemGroupSpec = ItemGroupSpec.DEFAULT
        set(value) {
            field = value
            planksCollection.itemGroup = value
        }

    init {
        plankProperties
            .hardnessAndResistance(2f * hardnessMultiplier, 2f * resistanceMultiplier)
            .sound(SoundType.WOOD)
            .fireInfo(5, 20)

        logProperties
            .hardnessAndResistance(2f * hardnessMultiplier, 2f * resistanceMultiplier)
            .sound(SoundType.WOOD)
            .fireInfo(5, 5)
    }

    public val planks: BlockSpec by lazy {
        planksCollection.full
            .datagen {
                tags(BlockTags.PLANKS)
            }
    }

    public val slab: BlockSpec by lazy {
        planksCollection.slab
            .datagen {
                tags(BlockTags.WOODEN_SLABS)
            }
    }

    public val stairs: BlockSpec by lazy {
        planksCollection.stairs
            .datagen {
                tags(BlockTags.WOODEN_STAIRS)
            }
    }

    public val fence: BlockSpec by lazy {
        planksCollection.fence
    }

    public val fenceGate: BlockSpec by lazy {
        planksCollection.fenceGate
    }

    public val logTag: Tag<Block> by lazy {
        val tag = TagWrappers.block(registrationManager.modid, woodName + "_logs")
        registrationManager.datagen.blockTags.meta(BlockTags.LOGS, tag)
        tag
    }
    public val logItemTag: Tag<Item> by lazy {
        val tag = TagWrappers.itemFormOf(logTag)
        registrationManager.datagen.blockTags.addItemForm(logTag, tag)
        tag
    }

    public val log: BlockSpec by lazy {
        BlockSpec(woodName + "_log")
            .withProperties(logProperties)
            .mapColor(barkColor)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .datagen {
                tags(logTag)
                logItemTag // create and register it
            }
            .block { BaseLogBlock(woodColor, it.blockProperties) }
    }

    public val strippedLog: BlockSpec by lazy {
        BlockSpec("stripped_" + woodName + "_log")
            .withProperties(logProperties)
            .mapColor(woodColor)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .datagen {
                tags(logTag)
                logItemTag // create and register it
            }
            .block { BaseLogBlock(woodColor, it.blockProperties) }
    }

    /**
     * The base name for the all-stripped [strippedWood] and all-bark [wood] blocks. Defaults to `<woodName>_wood`, or
     * just the [woodName] if it already ends in `wood`.
     */
    public var bareWoodName: String = if(woodName.endsWith("wood")) woodName else woodName + "_wood"

    /**
     * The all-stipped log variant
     */
    public val strippedWood: BlockSpec by lazy {
        BlockSpec("stripped_$bareWoodName")
            .withProperties(logProperties)
            .mapColor(woodColor)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .datagen {
                tags(logTag)
                logItemTag // create and register it
            }
            .block { BaseRotatedPillarBlock(it.blockProperties) }
            .datagen {
                model {
                    val tex = modLoc("block/stripped_" + woodName + "_log")
                    axisBlock(block as RotatedPillarBlock, tex, tex)
                }
            }
    }

    /**
     * The all-bark log variant
     */
    public val wood: BlockSpec by lazy {
        BlockSpec(bareWoodName)
            .withProperties(logProperties)
            .mapColor(barkColor)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .datagen {
                tags(logTag)
                logItemTag // create and register it
            }
            .block { BaseRotatedPillarBlock(it.blockProperties) }
            .datagen {
                model {
                    val tex = modLoc("block/" + woodName + "_log")
                    axisBlock(block as RotatedPillarBlock, tex, tex)
                }
            }
    }

    public val leaves: BlockSpec by lazy {
        BlockSpec(woodName + "_leaves")
            .withProperties(BaseLeavesBlock.defaultProperties)
            .block {
                BaseLeavesBlock(it.blockProperties)
            }

    }

//    + planks: BuildingBlockProducts
//    + log
//    + stripped log
//    + stripped
//    + bark
//    - trapdoor
//    - door
//    - sign
//    - button?
//    - pressure plate?
//    # tree stuff:
//    + leaves
//    - sapling
//    - chest? (chest variants aren't in vanilla)

}
package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.foundation.block.*
import net.minecraft.tags.BlockTags

/**
 * @param fullName The name of the full block (e.g. "oak_planks")
 * @param specialName The name prefix of the special block types (e.g. "oak" becomes "oak_slab", "oak_fence", etc.)
 */
public class BuildingBlockCollection(private val fullName: String, private val specialName: String) {
    /**
     * Custom block properties to apply to all the blocks in this collection
     */
    public val blockProperties: FoundationBlockProperties = FoundationBlockProperties()

    /**
     * What layer the blocks should be rendered in
     */
    public var renderLayer: RenderLayerSpec = RenderLayerSpec.SOLID

    /**
     * The item group to put the blocks in
     */
    public var itemGroup: ItemGroupSpec = ItemGroupSpec.DEFAULT

    /**
     * Whether to generate recipes for the special block types
     */
    public var datagenRecipes: Boolean = true // unimplemented

    public val full: BlockSpec by lazy {
        BlockSpec(fullName)
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .block { BaseBlock(it.blockProperties) }
    }

    public val slab: BlockSpec by lazy {
        BlockSpec(specialName + "_slab")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .block { BaseSlabBlock(it.blockProperties, fullName) }
            .datagen {
                tags(BlockTags.SLABS)
            }
    }

    public val stairs: BlockSpec by lazy {
        BlockSpec(specialName + "_stairs")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .block { BaseStairsBlock({ full.lazy.get().defaultState }, it.blockProperties, fullName) }
            .datagen {
                tags(BlockTags.STAIRS)
            }
    }

    public val fence: BlockSpec by lazy {
        BlockSpec(specialName + "_fence")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .block { BaseFenceBlock(it.blockProperties, fullName) }
            .datagen {
                tags(BlockTags.FENCES)
            }
    }

    public val fenceGate: BlockSpec by lazy {
        BlockSpec(specialName + "_fence_gate")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .block { BaseFenceGateBlock(it.blockProperties, fullName) }
    }

    public val wall: BlockSpec by lazy {
        BlockSpec(specialName + "_wall")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .itemGroup(itemGroup)
            .block { BaseWallBlock(it.blockProperties, fullName) }
            .datagen {
                tags(BlockTags.WALLS)
            }
    }
}

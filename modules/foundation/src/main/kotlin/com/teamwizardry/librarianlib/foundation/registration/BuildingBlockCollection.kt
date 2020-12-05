package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.foundation.block.*

/**
 * @param fullName The name of the full block (e.g. "oak_planks")
 * @param specialName The name prefix of the special block types (e.g. "oak" becomes "oak_slab", "oak_fence", etc.)
 */
public class BuildingBlockCollection(private val fullName: String, private val specialName: String) {
    /**
     * Custom block properties to apply to all the blocks in this collection
     */
    public val blockProperties: DefaultProperties = DefaultProperties()

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
            .block { BaseSimpleBlock(it.blockProperties) }
    }

    public val slab: BlockSpec by lazy {
        BlockSpec(specialName + "_slab")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .block { BaseSlabBlock(it.blockProperties, fullName) }
    }

    public val stairs: BlockSpec by lazy {
        BlockSpec(specialName + "_stairs")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .block { BaseStairsBlock({ full.lazy.get().defaultState }, it.blockProperties, fullName) }
    }

    public val fence: BlockSpec by lazy {
        BlockSpec(specialName + "_fence")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .block { BaseFenceBlock(it.blockProperties, fullName) }
    }

    public val fenceGate: BlockSpec by lazy {
        BlockSpec(specialName + "_fence_gate")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .block { BaseFenceGateBlock(it.blockProperties, fullName) }
    }

    public val wall: BlockSpec by lazy {
        BlockSpec(specialName + "_wall")
            .withProperties(blockProperties)
            .renderLayer(renderLayer)
            .block { BaseWallBlock(it.blockProperties, fullName) }
    }
}
package com.teamwizardry.librarianlib.foundation.item

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.util.Identifier
import net.minecraftforge.client.model.generators.ItemModelProvider

public open class BaseBlockItem(blockIn: Block, builder: Properties): BlockItem(blockIn, builder), IFoundationItem {
    private var blockModel: Identifier? = null

    /**
     * Configures this item to use the specified block model
     */
    public fun useBlockModel(model: Identifier): BaseBlockItem {
        blockModel = model
        return this
    }

    override fun generateItemModel(gen: ItemModelProvider) {
        blockModel?.also { blockModel ->
            gen.withExistingParent(registryName!!.path, blockModel)
            return
        }
        super.generateItemModel(gen)
    }
}
package com.teamwizardry.librarianlib.foundation.item

import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.client.model.generators.ModelFile
import net.minecraftforge.common.extensions.IForgeItem

/**
 * An interface for implementing Foundation's extended item functionality.
 */
public interface IFoundationItem: IForgeItem {
    /**
     * Gets this item's inventory texture name (e.g. the default, `item/item_id`). This is used by the
     * default [generateItemModel] implementation.
     */
    public fun itemTextureName(): String {
        return "item/${item.registryName!!.path}"
    }

    /**
     * Generates the models for this item
     */
    public fun generateItemModel(gen: ItemModelProvider) {
        gen.getBuilder(item.registryName!!.path)
            .parent(ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", gen.modLoc(itemTextureName()))
    }
}

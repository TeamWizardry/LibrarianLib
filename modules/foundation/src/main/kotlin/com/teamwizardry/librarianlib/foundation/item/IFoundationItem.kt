package com.teamwizardry.librarianlib.foundation.item

import net.minecraftforge.client.model.generators.ItemModelProvider

/**
 * An interface for implementing Foundation's extended item functionality.
 */
interface IFoundationItem {
    /**
     * Generates the models for this item
     */
    fun generateItemModel(gen: ItemModelProvider)
}

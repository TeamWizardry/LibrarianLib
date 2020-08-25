package com.teamwizardry.librarianlib.foundation.block

import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * An interface for implementing Foundation's extended block functionality.
 */
interface IFoundationBlock {
    /**
     * Generates the models for this block
     */
    fun generateBlockState(gen: BlockStateProvider)
}
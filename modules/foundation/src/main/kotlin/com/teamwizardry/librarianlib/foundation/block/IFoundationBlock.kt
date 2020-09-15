package com.teamwizardry.librarianlib.foundation.block

import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.extensions.IForgeBlock

/**
 * An interface for implementing Foundation's extended block functionality.
 */
public interface IFoundationBlock: IForgeBlock {
    /**
     * Generates the models for this block
     */
    public fun generateBlockState(gen: BlockStateProvider) { }
}
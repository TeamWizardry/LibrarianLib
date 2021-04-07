package com.teamwizardry.librarianlib.core.util;

import net.minecraft.block.Block;
import net.minecraft.tags.ITag;

/**
 * This class purely exists as an easy spot check for whether obfuscation is working correctly.
 *
 * It's package-private so it doesn't show up in any autocomplete stuff.
 */
class ObfCheck extends Block {
    public ObfCheck(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isIn(ITag<Block> tagIn) {
        String shouldBe = "isIn";
        return super.isIn(tagIn);
    }
}

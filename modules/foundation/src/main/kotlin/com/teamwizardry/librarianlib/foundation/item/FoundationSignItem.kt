package com.teamwizardry.librarianlib.foundation.item

import net.minecraft.block.Block
import net.minecraft.item.SignItem

public class FoundationSignItem(propertiesIn: Properties, standingBlock: Block, wallBlock: Block):
    SignItem(propertiesIn, standingBlock, wallBlock), IFoundationItem {
}
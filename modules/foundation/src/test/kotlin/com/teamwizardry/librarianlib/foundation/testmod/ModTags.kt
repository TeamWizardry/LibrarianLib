package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.util.TagWrappers
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.tags.BlockTags
import net.minecraft.tags.Tag

object ModTags {
    val STRANGE_BLOCK: Tag<Block> = TagWrappers.block(LibrarianLibFoundationTestMod.modid, "strange")
    val STRANGE_ITEM: Tag<Item> = TagWrappers.itemFormOf(STRANGE_BLOCK)

    internal fun registerTagsDatagen(registrationManager: RegistrationManager) {
        // add contents of STRANGE_TAG to WOOL
        registrationManager.datagen.blockTags.meta(BlockTags.WOOL, STRANGE_BLOCK)
        registrationManager.datagen.blockTags.addItemForm(STRANGE_BLOCK, STRANGE_ITEM)
    }
}

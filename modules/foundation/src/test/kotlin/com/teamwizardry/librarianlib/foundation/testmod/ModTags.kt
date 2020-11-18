package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.util.TagWrappers
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import net.minecraft.block.Block
import net.minecraft.tags.BlockTags
import net.minecraft.tags.Tag

object ModTags {
    val TEST_LOGS: Tag<Block> = TagWrappers.block(LibrarianLibFoundationTestMod.modid, "test_logs")

    internal fun registerTagsDatagen(registrationManager: RegistrationManager) {
        registrationManager.datagen.blockTags.meta(BlockTags.LOGS, TEST_LOGS)
    }
}

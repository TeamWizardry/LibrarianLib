package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.SimpleTagFactory
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import net.minecraft.block.Block
import net.minecraft.tags.BlockTags
import net.minecraft.tags.Tag

object ModTags {
    private val tags = SimpleTagFactory(LibrarianLibFoundationTestMod.modid)
    val TEST_LOGS: Tag<Block> = tags.block("test_logs")

    internal fun registerTagsDatagen(registrationManager: RegistrationManager) {
        registrationManager.datagen.blockTags.meta(BlockTags.LOGS, TEST_LOGS)
    }
}

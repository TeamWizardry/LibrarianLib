package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.BaseMod
import com.teamwizardry.librarianlib.foundation.BaseTagFactory
import com.teamwizardry.librarianlib.foundation.block.BaseLogBlock
import com.teamwizardry.librarianlib.foundation.registration.BlockSpec
import com.teamwizardry.librarianlib.foundation.registration.ItemSpec
import com.teamwizardry.librarianlib.foundation.registration.LazyItem
import com.teamwizardry.librarianlib.foundation.registration.RenderLayerSpec
import com.teamwizardry.librarianlib.foundation.registration.TileEntitySpec
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestTileBlock
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestTileEntity
import net.minecraft.block.Block
import net.minecraft.block.material.MaterialColor
import net.minecraft.item.Item
import net.minecraft.tags.BlockTags
import net.minecraft.tags.Tag
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import java.util.function.Function

@Mod("librarianlib-foundation-test")
object LibrarianLibFoundationTestMod: BaseMod(true) {
    init {
        ModTiles.registerTileEntities(registrationManager)
        ModBlocks.registerBlocks(registrationManager)
        ModItems.registerItems(registrationManager)

        registrationManager.itemGroupIcon = ModItems.testItem
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Foundation Test")

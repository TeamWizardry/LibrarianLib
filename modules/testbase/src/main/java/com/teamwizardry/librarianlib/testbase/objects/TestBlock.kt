package com.teamwizardry.librarianlib.testbase.objects

import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.ModLoadingContext

class TestBlock(val config: TestBlockConfig): Block(config.properties) {
    init {
        this.registryName = ResourceLocation(ModLoadingContext.get().activeContainer.modId, config.id)
    }

}
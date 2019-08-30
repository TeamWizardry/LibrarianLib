package com.teamwizardry.librarianlib.testbase.objects

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.block.material.PushReaction
import net.minecraftforge.fml.ModLoadingContext

@TestObjectDslMarker
class TestBlockConfig(val id: String, val name: String) {
    constructor(id: String, name: String, config: TestBlockConfig.() -> Unit): this(id, name) {
        this.config()
    }

    val modid: String = ModLoadingContext.get().activeContainer.modId

    val properties: Block.Properties = Block.Properties.create(testMaterial)

    var transparent: Boolean = false
    var directional: Boolean = false

    companion object {
        val testMaterial: Material = Material(
            MaterialColor.PINK, // materialMapColorIn
            false, // liquid
            true, // solid
            true, // doesBlockMovement
            false, // opaque
            true, // requiresNoToolIn
            false, // canBurnIn
            false, // replaceableIn
            PushReaction.NORMAL // mobilityFlag
        )
    }

    @TestObjectDslMarker
    inner class Actions internal constructor() {

    }
}

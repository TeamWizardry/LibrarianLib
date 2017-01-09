package com.teamwizardry.librarianlib.test.variants

import com.teamwizardry.librarianlib.common.base.block.BlockModSlab
import com.teamwizardry.librarianlib.common.base.block.BlockModVariant
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraft.block.material.Material
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * @author WireSegal
 * Created at 5:10 PM on 1/8/17.
 */
object VariantEntryPoint : TestEntryPoint {

    lateinit var block: BlockModVariant
    lateinit var slabA: BlockModSlab
    lateinit var slabB: BlockModSlab
    lateinit var slabC: BlockModSlab

    override fun preInit(event: FMLPreInitializationEvent) {
        block = BlockModVariant("variant", Material.ROCK, "a", "b", "c")
        slabA = BlockModSlab("a_slab", block.defaultState)
        slabB = BlockModSlab("b_slab", block.defaultState.withProperty(block.property, "b"))
        slabC = BlockModSlab("c_slab", block.defaultState.withProperty(block.property, "c"))
    }

    override fun init(event: FMLInitializationEvent) {
        // NO-OP
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        // NO-OP
    }
}

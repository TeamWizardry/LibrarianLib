package com.teamwizardry.librarianlib.test.variants

import com.teamwizardry.librarianlib.common.base.block.*
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.util.*

/**
 * @author WireSegal
 * Created at 5:10 PM on 1/8/17.
 */
object VariantEntryPoint : TestEntryPoint {

    lateinit var sapling: BlockModSapling

    override fun preInit(event: FMLPreInitializationEvent) {
        val block = BlockModVariant("variant", Material.ROCK, "a", "b", "c")
        BlockModSlab("a_slab", block.defaultState)
        BlockModSlab("b_slab", block.defaultState.withProperty(block.property, "b"))
        BlockModSlab("c_slab", block.defaultState.withProperty(block.property, "c"))
        BlockModStairs("a_stairs", block.defaultState)
        BlockModStairs("b_stairs", block.defaultState.withProperty(block.property, "b"))
        BlockModStairs("c_stairs", block.defaultState.withProperty(block.property, "c"))

        val wood = BlockModLog("log")
        val leaves = object : BlockModLeaves("leaves") {
            override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
                return sapling.itemForm
            }
        }
        sapling = object : BlockModSapling("sapling") {
            override fun generateTree(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
                defaultSaplingBehavior(worldIn, pos, state, rand, wood, leaves)
            }
        }
    }

    override fun init(event: FMLInitializationEvent) {
        // NO-OP
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        // NO-OP
    }
}

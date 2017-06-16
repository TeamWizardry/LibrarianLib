package com.teamwizardry.librarianlib.test.variants

import com.teamwizardry.librarianlib.features.base.block.*
import com.teamwizardry.librarianlib.features.base.item.IGlowingItem
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.base.item.ItemModArrow
import com.teamwizardry.librarianlib.features.base.item.ItemModShield
import com.teamwizardry.librarianlib.features.kotlin.sendSpamlessMessage
import com.teamwizardry.librarianlib.test.module.BlockModule
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.entity.projectile.EntityTippedArrow
import net.minecraft.init.Items
import net.minecraft.init.PotionTypes
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionUtils
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

/**
 * @author WireSegal
 * Created at 5:10 PM on 1/8/17.
 */
object VariantEntryPoint : TestEntryPoint {

    lateinit var sapling: BlockModSapling

    override fun preInit(event: FMLPreInitializationEvent) {
        object : ItemModArrow("arrow") {
            override fun generateArrowEntity(worldIn: World, stack: ItemStack, position: Vec3d, shooter: EntityLivingBase?): EntityArrow {
                val arrow = if (shooter != null) EntityTippedArrow(worldIn, shooter)
                else EntityTippedArrow(worldIn, position.x, position.y, position.z)

                val s = ItemStack(Items.TIPPED_ARROW)
                PotionUtils.addPotionToItemStack(s, PotionTypes.INVISIBILITY)
                arrow.setPotionEffect(s)

                return arrow
            }

            override fun isInfinite(stack: ItemStack, bow: ItemStack, player: EntityPlayer): Boolean {
                return false
            }
        }

        object : ItemMod("glow"), IGlowingItem {
            @SideOnly(Side.CLIENT)
            override fun transformToGlow(itemStack: ItemStack, model: IBakedModel): IBakedModel? = IGlowingItem.Helper.wrapperBake(model, false, 1)

            override fun shouldDisableLightingForGlow(itemStack: ItemStack, model: IBakedModel): Boolean {
                return true
            }
        }

        val block = object : BlockModVariant("variant", Material.ROCK, "a", "b", "c") {
            override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
                playerIn.sendSpamlessMessage(state.getValue(property), 0x8008)
                return true
            }
        }

        BlockModPane("a_pane", true, block.defaultState)
        BlockModTrapdoor("a_trap", block.defaultState)
        BlockModDoor("a_door", block.defaultState)
        BlockModSlab("a_slab", block.defaultState)
        BlockModSlab("b_slab", block.defaultState.withProperty(block.property, "b"))
        BlockModSlab("c_slab", block.defaultState.withProperty(block.property, "c"))
        BlockModStairs("a_stairs", block.defaultState)
        BlockModStairs("b_stairs", block.defaultState.withProperty(block.property, "b"))
        BlockModStairs("c_stairs", block.defaultState.withProperty(block.property, "c"))
        BlockModFence("a_fence", block.defaultState)
        BlockModWall("a_wall", block.defaultState)
        BlockModFenceGate("a_gate", block.defaultState)

        BlockModule()

        ItemModShield("shield")

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

package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.client.core.ModelHandler
import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider
import com.teamwizardry.librarianlib.common.base.item.IModItemProvider
import com.teamwizardry.librarianlib.common.base.item.ISpecialModelProvider
import com.teamwizardry.librarianlib.common.util.currentModId
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * The default implementation for an IModBlock wrapper Item that gets registered as an IVariantHolder.
 */
@Suppress("LeakingThis")
open class ItemModBlock(block: Block) : ItemBlock(block), IModItemProvider, IBlockColorProvider, ISpecialModelProvider {

    private val modBlock: IModBlock
    private val modId: String

    init {
        this.modId = currentModId
        this.modBlock = block as IModBlock
        if (this.variants.size > 1)
            this.setHasSubtypes(true)
        ModelHandler.registerVariantHolder(this)
    }

    override fun getMetadata(damage: Int) = damage

    override fun setUnlocalizedName(par1Str: String): ItemBlock {
        val rl = ResourceLocation(modId, par1Str)
        GameRegistry.register(this, rl)
        return super.setUnlocalizedName(par1Str)
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        val dmg = stack.itemDamage
        val variants = this.variants
        val name = if (dmg >= variants.size) this.modBlock.bareName else variants[dmg]

        return "tile.$modId:$name"
    }

    override fun getSubItems(itemIn: Item, tab: CreativeTabs?, subItems: MutableList<ItemStack>) {
        variants.indices.mapTo(subItems) { ItemStack(itemIn, 1, it) }
    }

    override val providedItem: Item
        get() = this

    override val providedBlock: Block
        get() = block

    override val variants: Array<out String>
        get() = this.modBlock.variants

    override val meshDefinition: ((ItemStack) -> ModelResourceLocation)?
        get() = this.modBlock.meshDefinition

    override val itemColorFunction: ((ItemStack, Int) -> Int)?
        get() = if (this.modBlock is IItemColorProvider) modBlock.itemColorFunction else null

    override val blockColorFunction: ((IBlockState, IBlockAccess?, BlockPos?, Int) -> Int)?
        get() = if (this.modBlock is IBlockColorProvider) modBlock.blockColorFunction else null

    @SideOnly(Side.CLIENT)
    override fun getSpecialModel(index: Int) = if (this.modBlock is ISpecialModelProvider) this.modBlock.getSpecialModel(index) else null

    override fun getRarity(stack: ItemStack) = this.modBlock.getBlockRarity(stack)
}


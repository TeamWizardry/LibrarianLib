package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.core.common.RegistrationHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.base.item.ISpecialModelProvider
import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemSlab
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * The default implementation for an IModBlock wrapper Item that gets registered as an IVariantHolder.
 */
@Suppress("LeakingThis")
open class ItemModSlab(block: BlockModSlab) : ItemSlab(block, block.singleBlock, block.doubleBlock), IModItemProvider, IBlockColorProvider, ISpecialModelProvider, IModelGenerator {

    private val modBlock: IModBlock = block
    private val modId = currentModId

    init {
        if (this.variants.size > 1)
            this.setHasSubtypes(true)
        ModelHandler.registerVariantHolder(this)
    }

    override fun getMetadata(damage: Int) = damage

    override fun setTranslationKey(par1Str: String): ItemBlock {
        val rl = ResourceLocation(modId, par1Str)
        RegistrationHandler.register(this, rl)
        super.setTranslationKey(par1Str)
        return this
    }

    override fun getTranslationKey(stack: ItemStack): String {
        val dmg = stack.itemDamage
        val variants = this.variants
        val name = if (dmg >= variants.size) this.modBlock.bareName else variants[dmg]

        return "tile.$modId:$name"
    }

    override fun getSubItems(tab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        if (isInCreativeTab(tab))
            variants.indices.mapTo(subItems) { ItemStack(this, 1, it) }
    }


    override val itemForm: ItemBlock
        get() = this

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

    override val stateMapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?
        get() = this.modBlock.stateMapper

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?)
            = if (this.modBlock is IModelGenerator) modBlock.generateMissingBlockstate(block, mapper) else false

    override fun generateMissingItem(item: IModItemProvider, variant: String)
            = if (this.modBlock is IModelGenerator) modBlock.generateMissingItem(item, variant) else false

    @SideOnly(Side.CLIENT)
    override fun getSpecialModel(index: Int) = if (this.modBlock is ISpecialModelProvider) this.modBlock.getSpecialModel(index) else null

    override fun getRarity(stack: ItemStack) = this.modBlock.getBlockRarity(stack)
}


package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemTool
import net.minecraft.util.NonNullList

/**
 * The default implementation for an IVariantHolder tool.
 */
@Suppress("LeakingThis")
open class ItemModTool(name: String, attackDamage: Float, attackSpeed: Float, toolMaterial: ToolMaterial, effectiveBlocks: Set<Block>, vararg variants: String) : ItemTool(attackDamage, attackSpeed, toolMaterial, effectiveBlocks), IModItemProvider, IModelGenerator {

    companion object {
        private val AXE_EFFECTIVE_ON = setOf(Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER)
        private val PICKAXE_EFFECTIVE_ON = setOf(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB)
        private val SHOVEL_EFFECTIVE_ON = setOf(Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND)

        fun fromToolClass(toolClass: String) = when(toolClass) {
            "axe" -> AXE_EFFECTIVE_ON
            "shovel" -> SHOVEL_EFFECTIVE_ON
            "pickaxe" -> PICKAXE_EFFECTIVE_ON
            else -> emptySet()
        }
    }

    var toolClass: String? = null
        protected set

    constructor(name: String, attackDamage: Float, attackSpeed: Float, toolMaterial: ToolMaterial, toolClass: String, vararg variants: String) : this(name, attackDamage, attackSpeed, toolMaterial, fromToolClass(toolClass), *variants) {
        this.toolClass = toolClass
    }
    constructor(name: String, toolMaterial: ToolMaterial, effectiveBlocks: Set<Block>, vararg variants: String) : this(name, 0F, 0F, toolMaterial, effectiveBlocks, *variants)
    constructor(name: String, toolMaterial: ToolMaterial, toolClass: String, vararg variants: String) : this(name, 0F, 0F, toolMaterial, fromToolClass(toolClass), *variants)  {
        this.toolClass = toolClass
    }

    override val providedItem: Item
        get() = this

    private val bareName = VariantHelper.toSnakeCase(name)
    private val modId = currentModId
    override val variants = VariantHelper.setupItem(this, bareName, variants, creativeTab)

    override fun setUnlocalizedName(name: String): Item {
        VariantHelper.setUnlocalizedNameForItem(this, modId, name)
        return super.setUnlocalizedName(name)
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        val dmg = stack.itemDamage
        val variants = this.variants
        val name = if (dmg >= variants.size) this.bareName else variants[dmg]

        return "item.$modId:$name"
    }

    override fun getSubItems(itemIn: Item, tab: CreativeTabs?, subItems: NonNullList<ItemStack>) {
        variants.indices.mapTo(subItems) { ItemStack(itemIn, 1, it) }
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    open val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    override fun getHarvestLevel(stack: ItemStack, toolClass: String, player: EntityPlayer?, blockState: IBlockState?): Int {
        val level = super.getHarvestLevel(stack, toolClass, player, blockState)
        return if (level == -1 && toolClass == this.toolClass) this.toolMaterial.harvestLevel else level
    }

    override fun getToolClasses(stack: ItemStack): Set<String> {
        val clazz = toolClass
        return if (clazz != null) setOf(clazz) else super.getToolClasses(stack)
    }

    // Model Generation

    override fun generateMissingItem(variant: String): Boolean {
        ModelHandler.generateItemJson(this) {
            mapOf(JsonGenerationUtils.getPathForItemModel(this, variant)
                    to JsonGenerationUtils.generateBaseItemModel(this, variant, "item/handheld"))
        }
        return true
    }
}


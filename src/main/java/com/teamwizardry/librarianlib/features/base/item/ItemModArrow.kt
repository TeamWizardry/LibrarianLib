package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraft.block.BlockDispenser
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.dispenser.BehaviorProjectileDispense
import net.minecraft.dispenser.IPosition
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.item.Item
import net.minecraft.item.ItemArrow
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * The default implementation for an IVariantHolder arrow item.
 */
@Suppress("LeakingThis")
abstract class ItemModArrow(name: String, vararg variants: String) : ItemArrow(), IModItemProvider {

    override val providedItem: Item
        get() = this

    private val bareName = VariantHelper.toSnakeCase(name)
    private val modId = currentModId
    override val variants = VariantHelper.setupItem(this, bareName, variants, this::creativeTab)

    init {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, object : BehaviorProjectileDispense() {
            override fun getProjectileEntity(worldIn: World, position: IPosition, stackIn: ItemStack)
                    = generateArrowEntity(worldIn, stackIn, Vec3d(position.x, position.y, position.z), null)
        })
    }

    abstract fun generateArrowEntity(worldIn: World, stack: ItemStack, position: Vec3d, shooter: EntityLivingBase?): EntityArrow

    override fun createArrow(worldIn: World, stack: ItemStack, shooter: EntityLivingBase) = generateArrowEntity(worldIn, stack, shooter.positionVector, shooter)
    abstract override fun isInfinite(stack: ItemStack, bow: ItemStack, player: EntityPlayer): Boolean

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

    override fun getSubItems(tab: CreativeTabs?, subItems: NonNullList<ItemStack>) {
        if (isInCreativeTab(tab))
            variants.indices.mapTo(subItems) { ItemStack(this, 1, it) }
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    open val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]
}


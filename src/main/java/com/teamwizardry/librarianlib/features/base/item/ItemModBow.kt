package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.array
import com.teamwizardry.librarianlib.features.kotlin.jsonObject
import com.teamwizardry.librarianlib.features.kotlin.key
import com.teamwizardry.librarianlib.features.utilities.generateBaseItemModel
import com.teamwizardry.librarianlib.features.utilities.getPathForItemModel
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

/**
 * The default implementation for an IVariantHolder sword.
 */
@Suppress("LeakingThis")
open class ItemModBow(name: String) : ItemBow(), IModItemProvider, IModelGenerator {

    override val providedItem: Item
        get() = this

    private val bareName = VariantHelper.toSnakeCase(name)
    private val modId = currentModId
    override val variants = VariantHelper.setupItem(this, bareName, arrayOf(), this::creativeTab)

    override fun setTranslationKey(name: String): Item {
        VariantHelper.setTranslationKeyForItem(this, modId, name)
        return super.setTranslationKey(name)
    }

    override fun getTranslationKey(stack: ItemStack): String {
        val dmg = stack.itemDamage
        val variants = this.variants
        val name = if (dmg >= variants.size) this.bareName else variants[dmg]

        return "item.$modId:$name"
    }

    override fun getSubItems(tab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        if (isInCreativeTab(tab))
            variants.indices.mapTo(subItems) { ItemStack(this, 1, it) }
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    open val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    // Model Generation

    override fun generateMissingItem(item: IModItemProvider, variant: String): Boolean {
        val rd = this.key.namespace
        ModelHandler.generateItemJson(this) {
            getPathForItemModel(this, variant) to generateBaseItemModel(this, variant, "item/bow").apply {
                add("overrides", array(
                        jsonObject {
                            "predicate" {
                                "pulling" to 1
                            }
                            "model" to "$rd:item/${variant}_pulling_0"
                        },
                        jsonObject {
                            "predicate" {
                                "pulling" to 1
                                "pull" to 0.65
                            }
                            "model" to "$rd:item/${variant}_pulling_1"
                        },
                        jsonObject {
                            "predicate" to {
                                "pulling" to 1
                                "pull" to 0.9
                            }
                            "model" to "$rd:item/${variant}_pulling_2"
                        })
                )
            }

            for (i in 0..2)
                getPathForItemModel(this, variant + "_pulling_$i") to
                    generateBaseItemModel(this, variant + "_pulling_$i", "$rd:item/$variant")
        }
        return true
    }
}


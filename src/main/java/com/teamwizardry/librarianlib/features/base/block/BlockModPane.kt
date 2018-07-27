package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.block.BlockModSlab.Companion.wrapMaterial
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.jsonObject
import com.teamwizardry.librarianlib.features.kotlin.key
import com.teamwizardry.librarianlib.features.utilities.generateRegularItemModel
import com.teamwizardry.librarianlib.features.utilities.getPathForBaseBlockstate
import com.teamwizardry.librarianlib.features.utilities.getPathForBlockModel
import com.teamwizardry.librarianlib.features.utilities.getPathForItemModel
import net.minecraft.block.Block
import net.minecraft.block.BlockPane
import net.minecraft.block.material.MapColor
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 3:53 PM on 2/14/17.
 */
@Suppress("LeakingThis")
open class BlockModPane(name: String, canDrop: Boolean, val parent: IBlockState) : BlockPane(wrapMaterial(parent.material), canDrop), IModBlock, IModelGenerator {

    private val parentName = parent.block.registryName

    val modId: String = currentModId
    override val bareName: String = VariantHelper.toSnakeCase(name)
    override val variants: Array<out String> = VariantHelper.beginSetupBlock(name, arrayOf())

    override val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        VariantHelper.finishSetupBlock(this, bareName, itemForm, this::creativeTab)
    }

    override fun setUnlocalizedName(name: String): Block {
        super.setUnlocalizedName(name)
        VariantHelper.setUnlocalizedNameForBlock(this, modId, name, itemForm)
        return this
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor = parent.getMapColor(worldIn, pos)
    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    @Suppress("OverridingDeprecatedMember")
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)
    @Suppress("OverridingDeprecatedMember")
    @SideOnly(Side.CLIENT)
    override fun isTranslucent(state: IBlockState) = parent.isTranslucent
    override fun isToolEffective(type: String, state: IBlockState) = parent.block.isToolEffective(type, parent) || (blockMaterial == BlockModSlab.FAKE_WOOD && type == "axe")
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent) ?: if (blockMaterial == BlockModSlab.FAKE_WOOD) "axe" else null

    /**
     * Override this to have a custom ItemBlock implementation.
     */
    open fun createItemForm(): ItemBlock? {
        return ItemModBlock(this)
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    override val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName!!.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = key.resourcePath

        ModelHandler.generateBlockJson(this, {
            getPathForBaseBlockstate(this) to jsonObject {
                "multipart"(
                        jsonObject {
                            "apply" {
                                "model"("${registryName}_post")
                            }
                        },
                        jsonObject {
                            "when" {
                                "north"("true")
                            }
                            "apply" {
                                "model"("${registryName}_side")
                            }
                        },
                        jsonObject {
                            "when" {
                                "north"("true")
                            }
                            "apply" {
                                "model"("${registryName}_side")
                            }
                        },
                        jsonObject {
                            "when" {
                                "east"("true")
                            }
                            "apply" {
                                "model"("${registryName}_side")
                                "y"(90)
                            }
                        },
                        jsonObject {
                            "when" {
                                "south"("true")
                            }
                            "apply" {
                                "model"("${registryName}_side_alt")
                            }
                        },
                        jsonObject {
                            "when" {
                                "west"("true")
                            }
                            "apply" {
                                "model"("${registryName}_side_alt")
                                "y"(90)
                            }
                        },
                        jsonObject {
                            "when" {
                                "north"("true")
                            }
                            "apply" {
                                "model"("${registryName}_side")
                            }
                        },
                        jsonObject {
                            "when" {
                                "north"("false")
                            }
                            "apply" {
                                "model"("${registryName}_noside")
                            }
                        },
                        jsonObject {
                            "when" {
                                "east"("false")
                            }
                            "apply" {
                                "model"("${registryName}_noside")
                                "y"(90)
                            }
                        },
                        jsonObject {
                            "when" {
                                "south"("false")
                            }
                            "apply" {
                                "model"("${registryName}_noside_alt")
                            }
                        },
                        jsonObject {
                            "when" {
                                "west"("false")
                            }
                            "apply" {
                                "model"("${registryName}_noside_alt")
                                "y"(90)
                            }
                        }
                )
            }
        }, {
            getPathForBlockModel(this, "${simpleName}_post") to {
                "parent"("block/pane_post")
                "textures" {
                    "edge"(name + "_top")
                    "pane"(name)
                }
            }
            getPathForBlockModel(this, "${simpleName}_side") to {
                "parent"("block/pane_side")
                "textures" {
                    "edge"(name + "_top")
                    "pane"(name)
                }
            }
            getPathForBlockModel(this, "${simpleName}_side_alt") to {
                "parent"("block/pane_side_alt")
                "textures" {
                    "edge"(name + "_top")
                    "pane"(name)
                }
            }
            getPathForBlockModel(this, "${simpleName}_noside") to {
                "parent"("block/pane_noside")
                "textures" {
                    "pane"(name)
                }
            }
            getPathForBlockModel(this, "${simpleName}_noside_alt") to {
                "parent"("block/pane_noside_alt")
                "textures" {
                    "pane"(name)
                }
            }

        })
        return true
    }

    override fun generateMissingItem(item: IModItemProvider, variant: String): Boolean {
        ModelHandler.generateItemJson(item) {
            getPathForItemModel(this, variant) to generateRegularItemModel(this, variant)
        }
        return true
    }
}

package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.foundation.bridge.FoundationSignTileEntityCreator
import com.teamwizardry.librarianlib.foundation.bridge.ICustomSignMaterialBlock
import com.teamwizardry.librarianlib.foundation.item.BaseBlockItem
import com.teamwizardry.librarianlib.foundation.item.FoundationSignItem
import com.teamwizardry.librarianlib.foundation.registration.LazyBlock
import com.teamwizardry.librarianlib.foundation.registration.LazyTileEntityType
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.StandingSignBlock
import net.minecraft.block.WoodType
import net.minecraft.client.renderer.Atlases
import net.minecraft.client.renderer.model.Material
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.tileentity.SignTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.IBlockReader
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * The foundation standing sign block
 *
 * Required textures:
 * - `<modid>:entity/signs/<materialName>.png`
 * - `<particleTexture>.png`
 */
public open class FoundationStandingSignBlock(
    override val properties: FoundationBlockProperties,
    protected val materialName: String,
    protected val particleTexture: ResourceLocation,
    protected val wallSignBlock: LazyBlock,
    protected val tileEntityType: LazyTileEntityType<SignTileEntity>
): StandingSignBlock(properties.vanillaProperties, WoodType.OAK), IFoundationBlock, ICustomSignMaterialBlock {

    override fun signMaterial(): Material {
        return Material(Atlases.SIGN_ATLAS, signTexture(registryName!!.namespace, materialName))
    }

    override fun generateBlockState(gen: BlockStateProvider) {
        // empty model w/ particle texture
        gen.simpleBlock(this, gen.models().getBuilder(this.registryName!!.path).texture("particle", particleTexture))
    }

    override fun createBlockItem(itemProperties: Item.Properties): BlockItem {
        return FoundationSignItem(itemProperties, this, wallSignBlock.get())
    }

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return FoundationSignTileEntityCreator.create(tileEntityType.get())
    }

    internal companion object {
        fun signTexture(namespace: String, name: String): ResourceLocation {
            return loc(namespace, "entity/signs/$name")
        }
    }
}
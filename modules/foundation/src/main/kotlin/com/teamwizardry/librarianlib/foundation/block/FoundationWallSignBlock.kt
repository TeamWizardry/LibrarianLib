package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.foundation.bridge.FoundationSignTileEntityCreator
import com.teamwizardry.librarianlib.foundation.bridge.ICustomSignMaterialBlock
import com.teamwizardry.librarianlib.foundation.registration.LazyTileEntityType
import net.minecraft.block.BlockState
import net.minecraft.block.StandingSignBlock
import net.minecraft.block.WallSignBlock
import net.minecraft.block.WoodType
import net.minecraft.client.renderer.Atlases
import net.minecraft.client.renderer.model.RenderMaterial
import net.minecraft.tileentity.SignTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.IBlockReader
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * The foundation wall sign block
 *
 * Required models:
 * - `<modid>:block/<standingSignName>`
 */
public open class FoundationWallSignBlock(
    override val properties: FoundationBlockProperties,
    protected val materialName: String,
    protected val standingSignName: String,
    protected val tileEntityType: LazyTileEntityType<SignTileEntity>
): WallSignBlock(properties.vanillaProperties, WoodType.OAK), IFoundationBlock, ICustomSignMaterialBlock {

    override fun signMaterial(): RenderMaterial {
        return RenderMaterial(Atlases.SIGN_ATLAS, loc(registryName!!.namespace, "entity/signs/$materialName"))
    }

    override fun generateBlockState(gen: BlockStateProvider) {
        gen.simpleBlock(this, gen.models().getBuilder(standingSignName))
    }

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return FoundationSignTileEntityCreator.create(tileEntityType.get())
    }
}
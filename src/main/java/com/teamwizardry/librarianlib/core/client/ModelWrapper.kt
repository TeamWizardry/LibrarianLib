package com.teamwizardry.librarianlib.core.client

import com.sun.tools.doclets.internal.toolkit.util.DocPath.parent
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.IntPredicate
import java.util.function.Predicate

/**
 * @author WireSegal
 * Created at 4:13 PM on 4/29/17.
 */
@SideOnly(Side.CLIENT)
class ModelWrapper(val parent: IBakedModel, val allowUntinted: Boolean, val tintPredicate: IntPredicate) : IBakedModel by parent {

    constructor(parent: IBakedModel, allowUntinted: Boolean, tintPredicate: (Int) -> Boolean)
            : this(parent, allowUntinted, IntPredicate(tintPredicate))

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long) = parent.getQuads(state, side, rand).filter {
        (it.hasTintIndex() && (tintPredicate.test(it.tintIndex))) || allowUntinted
    }
}

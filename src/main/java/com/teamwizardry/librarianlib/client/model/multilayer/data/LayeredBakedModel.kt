package com.teamwizardry.librarianlib.client.model.multilayer.data

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

/**
 * Created by TheCodeWarrior
 */
private val EMPTY_LIST = listOf<BakedQuad>()
private val EMPTY_FACE_MAP = mapOf<EnumFacing, List<BakedQuad>>()

class LayeredBakedModel(protected val generalQuads: Map<BlockRenderLayer,List<BakedQuad>>, protected val faceQuads: Map<BlockRenderLayer,Map<EnumFacing, List<BakedQuad>>>, protected val ambientOcclusion: Boolean, protected val gui3d: Boolean, protected val texture: TextureAtlasSprite, protected val cameraTransforms: ItemCameraTransforms, protected val itemOverrideList: ItemOverrideList) : IBakedModel {

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
        val layer = MinecraftForgeClient.getRenderLayer()

        if(side == null) {
            return generalQuads[layer] ?: EMPTY_LIST
        } else {
            return (faceQuads[layer] ?: EMPTY_FACE_MAP)[side] ?: EMPTY_LIST
        }
    }

    override fun isAmbientOcclusion(): Boolean {
        return this.ambientOcclusion
    }

    override fun isGui3d(): Boolean {
        return this.gui3d
    }

    override fun isBuiltInRenderer(): Boolean {
        return false
    }

    override fun getParticleTexture(): TextureAtlasSprite {
        return this.texture
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return this.cameraTransforms
    }

    override fun getOverrides(): ItemOverrideList {
        return this.itemOverrideList
    }

    @SideOnly(Side.CLIENT)
    class Builder(p_i46988_1_: ModelBlock, p_i46988_2_: ItemOverrideList) : SimpleBakedModel.Builder(p_i46988_1_, p_i46988_2_) {
        private val builderGeneralQuads: MutableMap<BlockRenderLayer, MutableList<BakedQuad>> = EnumMap(BlockRenderLayer::class.java)
        private val builderFaceQuads: MutableMap<BlockRenderLayer, MutableMap<EnumFacing, MutableList<BakedQuad>>> = EnumMap(BlockRenderLayer::class.java)
        private var builderTexture: TextureAtlasSprite? = null
        private var builderLayer: BlockRenderLayer = BlockRenderLayer.SOLID

        private var builderAmbientOcclusion: Boolean = p_i46988_1_.isAmbientOcclusion
        private var builderGui3d: Boolean = p_i46988_1_.isGui3d
        private var builderCameraTransforms: ItemCameraTransforms = p_i46988_1_.allTransforms
        private var builderItemOverrideList: ItemOverrideList = p_i46988_2_

        init {
            for(layer in BlockRenderLayer.values()) {
                this.builderGeneralQuads[layer] = mutableListOf()
                this.builderFaceQuads[layer] = EnumMap(EnumFacing::class.java)

                this.builderFaceQuads[layer]?.let {
                    for (enumfacing in EnumFacing.values()) {
                        it.put(enumfacing, mutableListOf())
                    }
                }
            }
        }

        private fun addFaceQuads(p_188644_1_: IBlockState, p_188644_2_: IBakedModel, p_188644_3_: TextureAtlasSprite, p_188644_4_: EnumFacing, p_188644_5_: Long) {
            for (bakedquad in p_188644_2_.getQuads(p_188644_1_, p_188644_4_, p_188644_5_)) {
                this.addFaceQuad(p_188644_4_, BakedQuadRetextured(bakedquad, p_188644_3_))
            }
        }

        private fun addGeneralQuads(p_188645_1_: IBlockState, p_188645_2_: IBakedModel, p_188645_3_: TextureAtlasSprite, p_188645_4_: Long) {
            for (bakedquad in p_188645_2_.getQuads(p_188645_1_, null as EnumFacing, p_188645_4_)) {
                this.addGeneralQuad(BakedQuadRetextured(bakedquad, p_188645_3_))
            }
        }

        fun nextLayer(layer: BlockRenderLayer): LayeredBakedModel.Builder {
            builderLayer = layer
            return this
        }

        override fun addFaceQuad(facing: EnumFacing, quad: BakedQuad): LayeredBakedModel.Builder {
            this.builderFaceQuads[builderLayer]?.get(facing)?.add(quad)
            return this
        }

        override fun addGeneralQuad(quad: BakedQuad): LayeredBakedModel.Builder {
            this.builderGeneralQuads[builderLayer]?.add(quad)
            return this
        }

        override fun setTexture(texture: TextureAtlasSprite): LayeredBakedModel.Builder {
            this.builderTexture = texture
            return this
        }

        override fun makeBakedModel(): IBakedModel {
            if (this.builderTexture == null) {
                throw RuntimeException("Missing particle!")
            } else {
                return LayeredBakedModel(this.builderGeneralQuads, this.builderFaceQuads, this.builderAmbientOcclusion, this.builderGui3d, this.builderTexture!!, this.builderCameraTransforms, this.builderItemOverrideList)
            }
        }
    }
}

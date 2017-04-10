package com.teamwizardry.librarianlib.features.multilayermodel

import com.google.common.base.Function
import com.google.common.base.Optional
import com.google.common.collect.*
import com.teamwizardry.librarianlib.features.methodhandles.mhStaticGetter
import com.teamwizardry.librarianlib.features.methodhandles.mhValDelegate
import com.teamwizardry.librarianlib.features.multilayermodel.data.LayeredBakedModel
import com.teamwizardry.librarianlib.features.multilayermodel.data.LibLibBlockPart
import com.teamwizardry.librarianlib.features.multilayermodel.data.LibLibBlockPartFace
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.*
import net.minecraftforge.client.model.animation.AnimationItemOverrideList
import net.minecraftforge.client.model.animation.IAnimatedModel
import net.minecraftforge.client.model.animation.ModelBlockAnimation
import net.minecraftforge.common.model.IModelPart
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.model.Models
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.model.animation.IClip
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.Properties

/**
 * Created by TheCodeWarrior
 */

private val ModelBakery_mh_MODEL_GENERATED = ModelBakery::class.java.mhStaticGetter<ModelBlock>("MODEL_GENERATED", "field_177606_o")
private val ModelBakery_mh_MODEL_ENTITY = ModelBakery::class.java.mhStaticGetter<ModelBlock>("MODEL_ENTITY", "field_177616_r")

// because protected type returned by public method
private val ModelBlockAnimation.clips_mh by ModelBlockAnimation::class.java.mhValDelegate<ModelBlockAnimation, ImmutableMap<String, IClip>>("clips")

private val faceBakery = FaceBakery()

private fun isCustomRenderer(p_177587_1_: ModelBlock?): Boolean {
    if (p_177587_1_ == null) {
        return false
    } else {
        val modelblock = p_177587_1_.rootModel
        return modelblock === ModelBakery_mh_MODEL_ENTITY()
    }
}

private fun hasItemModel(p_177581_1_: ModelBlock?): Boolean {
    return if (p_177581_1_ == null) false else p_177581_1_.rootModel === ModelBakery_mh_MODEL_GENERATED()
}

private fun makeBakedQuad(p_177589_1_: BlockPart, p_177589_2_: BlockPartFace, p_177589_3_: TextureAtlasSprite, p_177589_4_: EnumFacing, p_177589_5_: net.minecraftforge.common.model.ITransformation, p_177589_6_: Boolean): BakedQuad {
    return faceBakery.makeBakedQuad(p_177589_1_.positionFrom, p_177589_1_.positionTo, p_177589_2_, p_177589_3_, p_177589_4_, p_177589_5_, p_177589_1_.partRotation, p_177589_6_, p_177589_1_.shade)
}


class LibLibModelWrapper(private val location: ResourceLocation, private val model: ModelBlock, private val uvlock: Boolean, private val animation: ModelBlockAnimation) : IRetexturableModel, IModelSimpleProperties, IModelUVLock, IAnimatedModel {

    override fun getDependencies(): Collection<ResourceLocation> {
        val set = Sets.newHashSet<ResourceLocation>()
        for (dep in model.overrideLocations) {
            if (location != dep) {
                set.add(dep)
                // CAN'T IMPLEMENT:
                // xTODO: check if this can go somewhere else, random access to global things is bad
                // stateModels.put(getInventoryVariant(dep.toString()), ModelLoaderRegistry.getModelOrLogError(dep, "Could not load override model $dep for model $location"))
            }
        }
        if (model.parentLocation != null && !model.parentLocation!!.resourcePath.startsWith("builtin/")) {
            set.add(model.parentLocation)
        }
        return ImmutableSet.copyOf(set)
    }

    override fun getTextures(): Collection<ResourceLocation> {
        // setting parent here to make textures resolve properly
        if (model.parentLocation != null) {
            if (model.parentLocation!!.resourcePath == "builtin/generated") {
                model.parent = ModelBakery_mh_MODEL_GENERATED()
            } else {
                val parent = ModelLoaderRegistry.getModelOrLogError(model.parentLocation, "Could not load vanilla model parent '" + model.parentLocation + "' for '" + model)
                if (parent is LibLibModelWrapper) {
                    model.parent = parent.model
                } else {
                    throw IllegalStateException("vanilla model '$model' can't have non-vanilla parent")
                }
            }
        }

        val builder = ImmutableSet.builder<ResourceLocation>()

        // CAN'T IMPLEMENT:
        if (hasItemModel(model)) {
            for (s in ItemModelGenerator.LAYERS) {
                val r = model.resolveTextureName(s)
                val loc = ResourceLocation(r)
                if (r != s) {
                    builder.add(loc)
                }
            }
        }
        for (s in model.textures.values) {
            if (!s.startsWith("#")) {
                builder.add(ResourceLocation(s))
            }
        }
        return builder.build()
    }

    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        if (!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT)) {
            throw IllegalArgumentException("can't bake vanilla models to the format that doesn't fit into the default one: " + format)
        }

        val newTransforms = Lists.newArrayList<TRSRTransformation>()
        for (i in 0..model.elements.size - 1) {
            val part = model.elements[i]
            newTransforms.add(animation.getPartTransform(state, part, i))
        }

        val transforms = model.allTransforms
        val tMap = Maps.newHashMap<ItemCameraTransforms.TransformType, TRSRTransformation>()
        tMap.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(transforms))
        tMap.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(state))
        val perState = SimpleModelState(ImmutableMap.copyOf(tMap))

        if (hasItemModel(model)) {
            return ItemLayerModel(model).bake(perState, format, bakedTextureGetter)
        }
        if (isCustomRenderer(model)) return BuiltInModel(transforms, model.createOverrides())
        return bakeNormal(model, perState, state, newTransforms, format, bakedTextureGetter, uvlock)
    }

    private fun bakeNormal(model: ModelBlock, perState: IModelState, modelState: IModelState, newTransforms: List<TRSRTransformation?>, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>, uvLocked: Boolean): IBakedModel {
        val baseState = modelState.apply(Optional.absent<IModelPart>()).or(TRSRTransformation.identity())
        val particle = bakedTextureGetter.apply(ResourceLocation(model.resolveTextureName("particle")))
        val builder = if (model.elements.any {
            it is LibLibBlockPart || it.mapFaces.any {
                val v = it.value
                v is LibLibBlockPartFace
            }
        })
            LayeredBakedModel.Builder(model, model.createOverrides()).setTexture(particle!!)
        else
            SimpleBakedModel.Builder(model, model.createOverrides()).setTexture(particle!!)
        for (i in 0..model.elements.size - 1) {
            if (modelState.apply(Optional.of(Models.getHiddenModelPart(ImmutableList.of(Integer.toString(i))))).isPresent) {
                continue
            }
            var part = model.elements[i]
            val transformation = if (newTransforms[i] == null) baseState else baseState.compose(newTransforms[i])
            var rot: BlockPartRotation? = part.partRotation
            if (rot == null) rot = BlockPartRotation(org.lwjgl.util.vector.Vector3f(), EnumFacing.Axis.Y, 0f, false)
            part = BlockPart(part.positionFrom, part.positionTo, part.mapFaces, rot, part.shade)
            for ((key, value) in part.mapFaces) {
                val textureatlassprite1 = bakedTextureGetter.apply(ResourceLocation(model.resolveTextureName(value.texture)))

                if (builder is LayeredBakedModel.Builder) {
                    builder.nextLayer((value as? LibLibBlockPartFace)?.layer ?: (part as? LibLibBlockPart)?.layer ?: BlockRenderLayer.SOLID)
                }

                if (value.cullFace == null || !TRSRTransformation.isInteger(transformation.matrix)) {
                    builder.addGeneralQuad(makeBakedQuad(part, value, textureatlassprite1!!, key, transformation, uvLocked))
                } else {
                    builder.addFaceQuad(baseState.rotate(value.cullFace), makeBakedQuad(part, value, textureatlassprite1!!, key, transformation, uvLocked))
                }
            }
        }

        return object : IPerspectiveAwareModel.MapWrapper(builder.makeBakedModel(), perState) {
            private val overrides = AnimationItemOverrideList(this@LibLibModelWrapper, modelState, format, bakedTextureGetter, super.getOverrides())

            override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
                if (state is IExtendedBlockState) {
                    if (state.unlistedNames.contains(Properties.AnimationProperty)) {
                        val newState = state.getValue(Properties.AnimationProperty)
                        val newExState = state.withProperty(Properties.AnimationProperty, null)
                        if (newState != null) {
                            return this@LibLibModelWrapper.bake(ModelStateComposition(modelState, newState), format, bakedTextureGetter).getQuads(newExState, side, rand)
                        }
                    }
                }
                return super.getQuads(state, side, rand)
            }

            override fun getOverrides(): ItemOverrideList {
                return overrides
            }
        }
    }

    override fun retexture(textures: ImmutableMap<String, String>): LibLibModelWrapper {
        if (textures.isEmpty())
            return this

        val elements = Lists.newArrayList<BlockPart>() //We have to duplicate this so we can edit it below.
        for (part in this.model.elements) {
            elements.add(BlockPart(part.positionFrom, part.positionTo, Maps.newHashMap(part.mapFaces), part.partRotation, part.shade))
        }

        val newModel = ModelBlock(this.model.parentLocation, elements,
                Maps.newHashMap(this.model.textures), this.model.isAmbientOcclusion, this.model.isGui3d, //New Textures man VERY IMPORTANT
                model.allTransforms, Lists.newArrayList(model.overrides))
        newModel.name = this.model.name
        newModel.parent = this.model.parent

        val removed = Sets.newHashSet<String>()

        for ((key, value) in textures) {
            if ("" == value) {
                removed.add(key)
                newModel.textures.remove(key)
            } else
                newModel.textures.put(key, value)
        }

        // Map the model's texture references as if it was the parent of a model with the retexture map as its textures.
        val remapped = Maps.newHashMap<String, String>()

        for ((key1, value) in newModel.textures) {
            if (value.startsWith("#")) {
                val key = value.substring(1)
                if (newModel.textures.containsKey(key))
                    remapped.put(key1, newModel.textures[key])
            }
        }

        newModel.textures.putAll(remapped)

        //Remove any faces that use a null texture, this is for performance reasons, also allows some cool layering stuff.
        for (part in newModel.elements) {
            val itr = part.mapFaces.entries.iterator()
            while (itr.hasNext()) {
                val entry = itr.next()
                if (removed.contains(entry.value.texture))
                    itr.remove()
            }
        }

        return LibLibModelWrapper(location, newModel, uvlock, animation)
    }

    override fun getClip(name: String): Optional<out IClip> {
        if (animation.clips_mh.containsKey(name)) {
            return Optional.fromNullable(animation.clips_mh[name])
        }
        return Optional.absent<IClip>()
    }

    override fun getDefaultState(): IModelState {
        return ModelRotation.X0_Y0
    }

    override fun smoothLighting(value: Boolean): LibLibModelWrapper {
        if (model.ambientOcclusion == value) {
            return this
        }
        val newModel = ModelBlock(model.parentLocation, model.elements, model.textures, value, model.isGui3d, model.allTransforms, Lists.newArrayList(model.overrides))
        newModel.parent = model.parent
        newModel.name = model.name
        return LibLibModelWrapper(location, newModel, uvlock, animation)
    }

    override fun gui3d(value: Boolean): LibLibModelWrapper {
        if (model.isGui3d == value) {
            return this
        }
        val newModel = ModelBlock(model.parentLocation, model.elements, model.textures, model.ambientOcclusion, value, model.allTransforms, Lists.newArrayList(model.overrides))
        newModel.parent = model.parent
        newModel.name = model.name
        return LibLibModelWrapper(location, newModel, uvlock, animation)
    }

    override fun uvlock(value: Boolean): IModel {
        if (uvlock == value) {
            return this
        }
        return LibLibModelWrapper(location, model, value, animation)
    }
}

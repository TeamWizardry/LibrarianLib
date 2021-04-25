package com.teamwizardry.librarianlib.core.rendering

import net.minecraft.client.render.RenderPhase

public object DefaultRenderPhases {
    @JvmField
    public val NO_TRANSPARENCY: RenderPhase.Transparency = RenderPhaseAccess._NO_TRANSPARENCY

    @JvmField
    public val ADDITIVE_TRANSPARENCY: RenderPhase.Transparency = RenderPhaseAccess._ADDITIVE_TRANSPARENCY

    @JvmField
    public val LIGHTNING_TRANSPARENCY: RenderPhase.Transparency = RenderPhaseAccess._LIGHTNING_TRANSPARENCY

    @JvmField
    public val GLINT_TRANSPARENCY: RenderPhase.Transparency = RenderPhaseAccess._GLINT_TRANSPARENCY

    @JvmField
    public val CRUMBLING_TRANSPARENCY: RenderPhase.Transparency = RenderPhaseAccess._CRUMBLING_TRANSPARENCY

    @JvmField
    public val TRANSLUCENT_TRANSPARENCY: RenderPhase.Transparency = RenderPhaseAccess._TRANSLUCENT_TRANSPARENCY

    @JvmField
    public val ZERO_ALPHA: RenderPhase.Alpha = RenderPhaseAccess._ZERO_ALPHA

    @JvmField
    public val ONE_TENTH_ALPHA: RenderPhase.Alpha = RenderPhaseAccess._ONE_TENTH_ALPHA

    @JvmField
    public val HALF_ALPHA: RenderPhase.Alpha = RenderPhaseAccess._HALF_ALPHA

    @JvmField
    public val SHADE_MODEL: RenderPhase.ShadeModel = RenderPhaseAccess._SHADE_MODEL

    @JvmField
    public val SMOOTH_SHADE_MODEL: RenderPhase.ShadeModel = RenderPhaseAccess._SMOOTH_SHADE_MODEL

    @JvmField
    public val MIPMAP_BLOCK_ATLAS_TEXTURE: RenderPhase.Texture = RenderPhaseAccess._MIPMAP_BLOCK_ATLAS_TEXTURE

    @JvmField
    public val BLOCK_ATLAS_TEXTURE: RenderPhase.Texture = RenderPhaseAccess._BLOCK_ATLAS_TEXTURE

    @JvmField
    public val NO_TEXTURE: RenderPhase.Texture = RenderPhaseAccess._NO_TEXTURE

    @JvmField
    public val DEFAULT_TEXTURING: RenderPhase.Texturing = RenderPhaseAccess._DEFAULT_TEXTURING

    @JvmField
    public val OUTLINE_TEXTURING: RenderPhase.Texturing = RenderPhaseAccess._OUTLINE_TEXTURING

    @JvmField
    public val GLINT_TEXTURING: RenderPhase.Texturing = RenderPhaseAccess._GLINT_TEXTURING

    @JvmField
    public val ENTITY_GLINT_TEXTURING: RenderPhase.Texturing = RenderPhaseAccess._ENTITY_GLINT_TEXTURING

    @JvmField
    public val ENABLE_LIGHTMAP: RenderPhase.Lightmap = RenderPhaseAccess._ENABLE_LIGHTMAP

    @JvmField
    public val DISABLE_LIGHTMAP: RenderPhase.Lightmap = RenderPhaseAccess._DISABLE_LIGHTMAP

    @JvmField
    public val ENABLE_OVERLAY_COLOR: RenderPhase.Overlay = RenderPhaseAccess._ENABLE_OVERLAY_COLOR

    @JvmField
    public val DISABLE_OVERLAY_COLOR: RenderPhase.Overlay = RenderPhaseAccess._DISABLE_OVERLAY_COLOR

    @JvmField
    public val ENABLE_DIFFUSE_LIGHTING: RenderPhase.DiffuseLighting = RenderPhaseAccess._ENABLE_DIFFUSE_LIGHTING

    @JvmField
    public val DISABLE_DIFFUSE_LIGHTING: RenderPhase.DiffuseLighting = RenderPhaseAccess._DISABLE_DIFFUSE_LIGHTING

    @JvmField
    public val ENABLE_CULLING: RenderPhase.Cull = RenderPhaseAccess._ENABLE_CULLING

    @JvmField
    public val DISABLE_CULLING: RenderPhase.Cull = RenderPhaseAccess._DISABLE_CULLING

    @JvmField
    public val ALWAYS_DEPTH_TEST: RenderPhase.DepthTest = RenderPhaseAccess._ALWAYS_DEPTH_TEST

    @JvmField
    public val EQUAL_DEPTH_TEST: RenderPhase.DepthTest = RenderPhaseAccess._EQUAL_DEPTH_TEST

    @JvmField
    public val LEQUAL_DEPTH_TEST: RenderPhase.DepthTest = RenderPhaseAccess._LEQUAL_DEPTH_TEST

    @JvmField
    public val ALL_MASK: RenderPhase.WriteMaskState = RenderPhaseAccess._ALL_MASK

    @JvmField
    public val COLOR_MASK: RenderPhase.WriteMaskState = RenderPhaseAccess._COLOR_MASK

    @JvmField
    public val DEPTH_MASK: RenderPhase.WriteMaskState = RenderPhaseAccess._DEPTH_MASK

    @JvmField
    public val NO_LAYERING: RenderPhase.Layering = RenderPhaseAccess._NO_LAYERING

    @JvmField
    public val POLYGON_OFFSET_LAYERING: RenderPhase.Layering = RenderPhaseAccess._POLYGON_OFFSET_LAYERING

    @JvmField
    public val VIEW_OFFSET_Z_LAYERING: RenderPhase.Layering = RenderPhaseAccess._VIEW_OFFSET_Z_LAYERING

    @JvmField
    public val NO_FOG: RenderPhase.Fog = RenderPhaseAccess._NO_FOG

    @JvmField
    public val FOG: RenderPhase.Fog = RenderPhaseAccess._FOG

    @JvmField
    public val BLACK_FOG: RenderPhase.Fog = RenderPhaseAccess._BLACK_FOG

    @JvmField
    public val MAIN_TARGET: RenderPhase.Target = RenderPhaseAccess._MAIN_TARGET

    @JvmField
    public val OUTLINE_TARGET: RenderPhase.Target = RenderPhaseAccess._OUTLINE_TARGET

    @JvmField
    public val TRANSLUCENT_TARGET: RenderPhase.Target = RenderPhaseAccess._TRANSLUCENT_TARGET

    @JvmField
    public val PARTICLES_TARGET: RenderPhase.Target = RenderPhaseAccess._PARTICLES_TARGET

    @JvmField
    public val WEATHER_TARGET: RenderPhase.Target = RenderPhaseAccess._WEATHER_TARGET

    @JvmField
    public val CLOUDS_TARGET: RenderPhase.Target = RenderPhaseAccess._CLOUDS_TARGET

    @JvmField
    public val ITEM_TARGET: RenderPhase.Target = RenderPhaseAccess._ITEM_TARGET

    @JvmField
    public val FULL_LINE_WIDTH: RenderPhase.LineWidth = RenderPhaseAccess._FULL_LINE_WIDTH
}

@Suppress("ObjectPropertyName")
private object RenderPhaseAccess : RenderPhase("", Runnable {}, Runnable {}) {
    val _NO_TRANSPARENCY: Transparency = NO_TRANSPARENCY
    val _ADDITIVE_TRANSPARENCY: Transparency = ADDITIVE_TRANSPARENCY
    val _LIGHTNING_TRANSPARENCY: Transparency = LIGHTNING_TRANSPARENCY
    val _GLINT_TRANSPARENCY: Transparency = GLINT_TRANSPARENCY
    val _CRUMBLING_TRANSPARENCY: Transparency = CRUMBLING_TRANSPARENCY
    val _TRANSLUCENT_TRANSPARENCY: Transparency = TRANSLUCENT_TRANSPARENCY
    val _ZERO_ALPHA: Alpha = ZERO_ALPHA
    val _ONE_TENTH_ALPHA: Alpha = ONE_TENTH_ALPHA
    val _HALF_ALPHA: Alpha = HALF_ALPHA
    val _SHADE_MODEL: ShadeModel = SHADE_MODEL
    val _SMOOTH_SHADE_MODEL: ShadeModel = SMOOTH_SHADE_MODEL
    val _MIPMAP_BLOCK_ATLAS_TEXTURE: Texture = MIPMAP_BLOCK_ATLAS_TEXTURE
    val _BLOCK_ATLAS_TEXTURE: Texture = BLOCK_ATLAS_TEXTURE
    val _NO_TEXTURE: Texture = NO_TEXTURE
    val _DEFAULT_TEXTURING: Texturing = DEFAULT_TEXTURING
    val _OUTLINE_TEXTURING: Texturing = OUTLINE_TEXTURING
    val _GLINT_TEXTURING: Texturing = GLINT_TEXTURING
    val _ENTITY_GLINT_TEXTURING: Texturing = ENTITY_GLINT_TEXTURING
    val _ENABLE_LIGHTMAP: Lightmap = ENABLE_LIGHTMAP
    val _DISABLE_LIGHTMAP: Lightmap = DISABLE_LIGHTMAP
    val _ENABLE_OVERLAY_COLOR: Overlay = ENABLE_OVERLAY_COLOR
    val _DISABLE_OVERLAY_COLOR: Overlay = DISABLE_OVERLAY_COLOR
    val _ENABLE_DIFFUSE_LIGHTING: DiffuseLighting = ENABLE_DIFFUSE_LIGHTING
    val _DISABLE_DIFFUSE_LIGHTING: DiffuseLighting = DISABLE_DIFFUSE_LIGHTING
    val _ENABLE_CULLING: Cull = ENABLE_CULLING
    val _DISABLE_CULLING: Cull = DISABLE_CULLING
    val _ALWAYS_DEPTH_TEST: DepthTest = ALWAYS_DEPTH_TEST
    val _EQUAL_DEPTH_TEST: DepthTest = EQUAL_DEPTH_TEST
    val _LEQUAL_DEPTH_TEST: DepthTest = LEQUAL_DEPTH_TEST
    val _ALL_MASK: WriteMaskState = ALL_MASK
    val _COLOR_MASK: WriteMaskState = COLOR_MASK
    val _DEPTH_MASK: WriteMaskState = DEPTH_MASK
    val _NO_LAYERING: Layering = NO_LAYERING
    val _POLYGON_OFFSET_LAYERING: Layering = POLYGON_OFFSET_LAYERING
    val _VIEW_OFFSET_Z_LAYERING: Layering = VIEW_OFFSET_Z_LAYERING
    val _NO_FOG: Fog = NO_FOG
    val _FOG: Fog = FOG
    val _BLACK_FOG: Fog = BLACK_FOG
    val _MAIN_TARGET: Target = MAIN_TARGET
    val _OUTLINE_TARGET: Target = OUTLINE_TARGET
    val _TRANSLUCENT_TARGET: Target = TRANSLUCENT_TARGET
    val _PARTICLES_TARGET: Target = PARTICLES_TARGET
    val _WEATHER_TARGET: Target = WEATHER_TARGET
    val _CLOUDS_TARGET: Target = CLOUDS_TARGET
    val _ITEM_TARGET: Target = ITEM_TARGET
    val _FULL_LINE_WIDTH: LineWidth = FULL_LINE_WIDTH
}

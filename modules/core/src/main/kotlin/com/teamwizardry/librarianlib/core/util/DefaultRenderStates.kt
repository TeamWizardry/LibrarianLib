package com.teamwizardry.librarianlib.core.util

import net.minecraft.client.renderer.RenderState

public object DefaultRenderStates {
    @JvmField
    public val NO_TRANSPARENCY: RenderState.TransparencyState = RenderStateAccess._NO_TRANSPARENCY

    @JvmField
    public val ADDITIVE_TRANSPARENCY: RenderState.TransparencyState = RenderStateAccess._ADDITIVE_TRANSPARENCY

    @JvmField
    public val LIGHTNING_TRANSPARENCY: RenderState.TransparencyState = RenderStateAccess._LIGHTNING_TRANSPARENCY

    @JvmField
    public val GLINT_TRANSPARENCY: RenderState.TransparencyState = RenderStateAccess._GLINT_TRANSPARENCY

    @JvmField
    public val CRUMBLING_TRANSPARENCY: RenderState.TransparencyState = RenderStateAccess._CRUMBLING_TRANSPARENCY

    @JvmField
    public val TRANSLUCENT_TRANSPARENCY: RenderState.TransparencyState = RenderStateAccess._TRANSLUCENT_TRANSPARENCY

    @JvmField
    public val ZERO_ALPHA: RenderState.AlphaState = RenderStateAccess._ZERO_ALPHA

    @JvmField
    public val DEFAULT_ALPHA: RenderState.AlphaState = RenderStateAccess._DEFAULT_ALPHA

    @JvmField
    public val HALF_ALPHA: RenderState.AlphaState = RenderStateAccess._HALF_ALPHA

    @JvmField
    public val SHADE_DISABLED: RenderState.ShadeModelState = RenderStateAccess._SHADE_DISABLED

    @JvmField
    public val SHADE_ENABLED: RenderState.ShadeModelState = RenderStateAccess._SHADE_ENABLED

    @JvmField
    public val BLOCK_SHEET_MIPPED: RenderState.TextureState = RenderStateAccess._BLOCK_SHEET_MIPPED

    @JvmField
    public val BLOCK_SHEET: RenderState.TextureState = RenderStateAccess._BLOCK_SHEET

    @JvmField
    public val NO_TEXTURE: RenderState.TextureState = RenderStateAccess._NO_TEXTURE

    @JvmField
    public val DEFAULT_TEXTURING: RenderState.TexturingState = RenderStateAccess._DEFAULT_TEXTURING

    @JvmField
    public val OUTLINE_TEXTURING: RenderState.TexturingState = RenderStateAccess._OUTLINE_TEXTURING

    @JvmField
    public val GLINT_TEXTURING: RenderState.TexturingState = RenderStateAccess._GLINT_TEXTURING

    @JvmField
    public val ENTITY_GLINT_TEXTURING: RenderState.TexturingState = RenderStateAccess._ENTITY_GLINT_TEXTURING

    @JvmField
    public val LIGHTMAP_ENABLED: RenderState.LightmapState = RenderStateAccess._LIGHTMAP_ENABLED

    @JvmField
    public val LIGHTMAP_DISABLED: RenderState.LightmapState = RenderStateAccess._LIGHTMAP_DISABLED

    @JvmField
    public val OVERLAY_ENABLED: RenderState.OverlayState = RenderStateAccess._OVERLAY_ENABLED

    @JvmField
    public val OVERLAY_DISABLED: RenderState.OverlayState = RenderStateAccess._OVERLAY_DISABLED

    @JvmField
    public val DIFFUSE_LIGHTING_ENABLED: RenderState.DiffuseLightingState = RenderStateAccess._DIFFUSE_LIGHTING_ENABLED

    @JvmField
    public val DIFFUSE_LIGHTING_DISABLED: RenderState.DiffuseLightingState =
        RenderStateAccess._DIFFUSE_LIGHTING_DISABLED

    @JvmField
    public val CULL_ENABLED: RenderState.CullState = RenderStateAccess._CULL_ENABLED

    @JvmField
    public val CULL_DISABLED: RenderState.CullState = RenderStateAccess._CULL_DISABLED

    @JvmField
    public val DEPTH_ALWAYS: RenderState.DepthTestState = RenderStateAccess._DEPTH_ALWAYS

    @JvmField
    public val DEPTH_EQUAL: RenderState.DepthTestState = RenderStateAccess._DEPTH_EQUAL

    @JvmField
    public val DEPTH_LEQUAL: RenderState.DepthTestState = RenderStateAccess._DEPTH_LEQUAL

    @JvmField
    public val COLOR_DEPTH_WRITE: RenderState.WriteMaskState = RenderStateAccess._COLOR_DEPTH_WRITE

    @JvmField
    public val COLOR_WRITE: RenderState.WriteMaskState = RenderStateAccess._COLOR_WRITE

    @JvmField
    public val DEPTH_WRITE: RenderState.WriteMaskState = RenderStateAccess._DEPTH_WRITE

    @JvmField
    public val NO_LAYERING: RenderState.LayerState = RenderStateAccess._NO_LAYERING

    @JvmField
    public val POLYGON_OFFSET_LAYERING: RenderState.LayerState = RenderStateAccess._POLYGON_OFFSET_LAYERING

    @JvmField
    public val PROJECTION_LAYERING: RenderState.LayerState = RenderStateAccess._PROJECTION_LAYERING

    @JvmField
    public val NO_FOG: RenderState.FogState = RenderStateAccess._NO_FOG

    @JvmField
    public val FOG: RenderState.FogState = RenderStateAccess._FOG

    @JvmField
    public val BLACK_FOG: RenderState.FogState = RenderStateAccess._BLACK_FOG

    @JvmField
    public val MAIN_TARGET: RenderState.TargetState = RenderStateAccess._MAIN_TARGET

    @JvmField
    public val OUTLINE_TARGET: RenderState.TargetState = RenderStateAccess._OUTLINE_TARGET

    @JvmField
    public val TRANSLUCENT_TARGET: RenderState.TargetState = RenderStateAccess._TRANSLUCENT_TARGET

    @JvmField
    public val PARTICLES_TARGET: RenderState.TargetState = RenderStateAccess._PARTICLES_TARGET

    @JvmField
    public val WEATHER_TARGET: RenderState.TargetState = RenderStateAccess._WEATHER_TARGET

    @JvmField
    public val CLOUDS_TARGET: RenderState.TargetState = RenderStateAccess._CLOUDS_TARGET

    @JvmField
    public val ITEM_ENTITY_TARGET: RenderState.TargetState = RenderStateAccess._ITEM_ENTITY_TARGET

    @JvmField
    public val DEFAULT_LINE: RenderState.LineState = RenderStateAccess._DEFAULT_LINE
}

@Suppress("ObjectPropertyName")
private object RenderStateAccess : RenderState("", Runnable {}, Runnable {}) {
    val _NO_TRANSPARENCY: TransparencyState = NO_TRANSPARENCY
    val _ADDITIVE_TRANSPARENCY: TransparencyState = ADDITIVE_TRANSPARENCY
    val _LIGHTNING_TRANSPARENCY: TransparencyState = LIGHTNING_TRANSPARENCY
    val _GLINT_TRANSPARENCY: TransparencyState = GLINT_TRANSPARENCY
    val _CRUMBLING_TRANSPARENCY: TransparencyState = CRUMBLING_TRANSPARENCY
    val _TRANSLUCENT_TRANSPARENCY: TransparencyState = TRANSLUCENT_TRANSPARENCY
    val _ZERO_ALPHA: AlphaState = ZERO_ALPHA
    val _DEFAULT_ALPHA: AlphaState = DEFAULT_ALPHA
    val _HALF_ALPHA: AlphaState = HALF_ALPHA
    val _SHADE_DISABLED: ShadeModelState = SHADE_DISABLED
    val _SHADE_ENABLED: ShadeModelState = SHADE_ENABLED
    val _BLOCK_SHEET_MIPPED: TextureState = BLOCK_SHEET_MIPPED
    val _BLOCK_SHEET: TextureState = BLOCK_SHEET
    val _NO_TEXTURE: TextureState = NO_TEXTURE
    val _DEFAULT_TEXTURING: TexturingState = DEFAULT_TEXTURING
    val _OUTLINE_TEXTURING: TexturingState = OUTLINE_TEXTURING
    val _GLINT_TEXTURING: TexturingState = GLINT_TEXTURING
    val _ENTITY_GLINT_TEXTURING: TexturingState = ENTITY_GLINT_TEXTURING
    val _LIGHTMAP_ENABLED: LightmapState = LIGHTMAP_ENABLED
    val _LIGHTMAP_DISABLED: LightmapState = LIGHTMAP_DISABLED
    val _OVERLAY_ENABLED: OverlayState = OVERLAY_ENABLED
    val _OVERLAY_DISABLED: OverlayState = OVERLAY_DISABLED
    val _DIFFUSE_LIGHTING_ENABLED: DiffuseLightingState = DIFFUSE_LIGHTING_ENABLED
    val _DIFFUSE_LIGHTING_DISABLED: DiffuseLightingState = DIFFUSE_LIGHTING_DISABLED
    val _CULL_ENABLED: CullState = CULL_ENABLED
    val _CULL_DISABLED: CullState = CULL_DISABLED
    val _DEPTH_ALWAYS: DepthTestState = DEPTH_ALWAYS
    val _DEPTH_EQUAL: DepthTestState = DEPTH_EQUAL
    val _DEPTH_LEQUAL: DepthTestState = DEPTH_LEQUAL
    val _COLOR_DEPTH_WRITE: WriteMaskState = COLOR_DEPTH_WRITE
    val _COLOR_WRITE: WriteMaskState = COLOR_WRITE
    val _DEPTH_WRITE: WriteMaskState = DEPTH_WRITE
    val _NO_LAYERING: LayerState = NO_LAYERING
    val _POLYGON_OFFSET_LAYERING: LayerState = POLYGON_OFFSET_LAYERING
    val _PROJECTION_LAYERING: LayerState = field_239235_M_
    val _NO_FOG: FogState = NO_FOG
    val _FOG: FogState = FOG
    val _BLACK_FOG: FogState = BLACK_FOG
    val _MAIN_TARGET: TargetState = MAIN_TARGET
    val _OUTLINE_TARGET: TargetState = OUTLINE_TARGET
    val _TRANSLUCENT_TARGET: TargetState = field_239236_S_
    val _PARTICLES_TARGET: TargetState = field_239237_T_
    val _WEATHER_TARGET: TargetState = field_239238_U_
    val _CLOUDS_TARGET: TargetState = field_239239_V_
    val _ITEM_ENTITY_TARGET: TargetState = field_241712_U_
    val _DEFAULT_LINE: LineState = DEFAULT_LINE
}

package com.teamwizardry.librarianlib.features.tesr

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import net.minecraft.client.renderer.BufferBuilder

abstract class TileRenderHandler<out T : TileMod>(@JvmField val tile: T) {
    /**
     * Render the tile entity using raw OpenGL. The origin is at the position of the tile, so no extra translation is
     * needed.
     *
     * This is only called if the @[TileRenderer]'s `fast` flag is not set to true.
     */
    open fun render(partialTicks: Float, destroyStage: Int, alpha: Float) {}

    /**
     * Render the tile entity to the passed buffer. The buffer's translation is set to the position of the tile, so no
     * extra translation is needed when adding vertices.
     *
     * This is only called if the @[TileRenderer]'s `fast` flag is set to true.
     */
    open fun renderFast(buffer: BufferBuilder, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {}
}
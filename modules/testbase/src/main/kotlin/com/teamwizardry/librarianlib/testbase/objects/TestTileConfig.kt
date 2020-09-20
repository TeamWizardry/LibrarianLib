package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.sided.ClientFunction
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType

public class TestTileConfig<T: TileEntity>(internal val factory: (TileEntityType<T>) -> T): TestConfig() {

    @get:JvmSynthetic
    internal var renderer: ClientFunction<in TileEntityRendererDispatcher, out TileEntityRenderer<*>>? = null
        private set

    /**
     * Sets the renderer factory for this tile type
     */
    public fun renderer(renderer: ClientFunction<in TileEntityRendererDispatcher, out TileEntityRenderer<*>>): TestTileConfig<T> {
        this.renderer = renderer
        return this
    }
}
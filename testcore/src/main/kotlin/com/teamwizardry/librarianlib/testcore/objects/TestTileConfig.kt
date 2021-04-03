package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.core.util.sided.ClientMetaSupplier
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType

public class TestTileConfig<T: TileEntity>(internal val factory: (TileEntityType<T>) -> T): TestConfig() {

    @get:JvmSynthetic
    internal var renderer: ClientMetaSupplier<TileEntityRendererFactory>? = null
        private set

    /**
     * Sets the renderer factory for this tile type
     */
    public fun renderer(renderer: ClientMetaSupplier<TileEntityRendererFactory>): TestTileConfig<T> {
        this.renderer = renderer
        return this
    }
}
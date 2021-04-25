package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.core.util.IncompleteBuilderException
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.core.util.sided.ClientMetaSupplier
import com.teamwizardry.librarianlib.core.util.sided.ClientSideFunction
import net.minecraft.block.Block
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.Identifier
import java.util.function.Supplier

/**
 * The specs for creating and registering a tile entity.
 */
public class TileEntitySpec<T: TileEntity>(
    /**
     * The registry name, sans mod ID.
     */
    public var id: String,
    /**
     * The tile entity factory.
     */
    public val factory: Supplier<T>
) {
    /**
     * The mod ID to register this tile entity under. This is populated by the [RegistrationManager].
     */
    public var modid: String = ""
        @JvmSynthetic
        internal set

    /**
     * The registry name of the tile entity type. The [mod ID][modid] is populated by the [RegistrationManager].
     */
    public val registryName: Identifier
        get() = Identifier(modid, id)

    internal val _validBlocks: MutableList<LazyBlock> = mutableListOf()

    /**
     * The list of blocks that this tile entity can exist for.
     */
    public val validBlocks: List<LazyBlock> = _validBlocks.unmodifiableView()

    /**
     * Manually add a valid block. Prefer using [BlockSpec.tileEntity] if you're using a BlockSpec.
     */
    public fun validBlock(block: Block): TileEntitySpec<T> {
        _validBlocks.add(LazyBlock(block))
        return this
    }

    @get:JvmSynthetic
    internal var renderer: ClientMetaSupplier<TileEntityRendererFactory>? = null
        private set

    /**
     * Sets the renderer factory for this tile type
     */
    public fun renderer(renderer: ClientMetaSupplier<TileEntityRendererFactory>): TileEntitySpec<T> {
        this.renderer = renderer
        return this
    }

    /**
     * The lazily-evaluated [TileEntityType] instance.
     */
    public val typeInstance: TileEntityType<T> by lazy {
        if (this.validBlocks.isEmpty())
            throw IncompleteBuilderException("Tile entity $registryName was never added to any blocks")
        val resolvedBlocks = this.validBlocks.map { it.get() }.toSet().toTypedArray()
        val type = TileEntityType.Builder.create(factory, *resolvedBlocks).build(null)
        type.registryName = registryName
        type
    }

    public val lazy: LazyTileEntityType<T> = LazyTileEntityType(this)
}

public fun interface TileEntityRendererFactory: ClientSideFunction {
    public fun create(dispatcher: TileEntityRendererDispatcher): TileEntityRenderer<*>
}

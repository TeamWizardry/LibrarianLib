package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.core.util.IncompleteBuilderException
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import java.util.function.Function
import java.util.function.Supplier

/**
 * The specs for creating and registering a tile entity.
 */
class TileEntitySpec(
    /**
     * The registry name, sans mod ID.
     */
    val name: String,
    /**
     * The tile entity factory.
     */
    val factory: Supplier<out TileEntity>
) {
    /**
     * The mod ID to register this tile entity under. This is populated by the [RegistrationManager].
     */
    var modid: String = ""
        @JvmSynthetic
        internal set

    /**
     * The registry name of the tile entity type. The [mod ID][modid] is populated by the [RegistrationManager].
     */
    val registryName: ResourceLocation
        get() = ResourceLocation(modid, name)


    internal val _validBlocks: MutableSet<LazyBlock> = mutableSetOf()

    /**
     * The list of blocks that this tile entity can exist for.
     */
    val validBlocks: Set<LazyBlock> = _validBlocks.unmodifiableView()

    /**
     * The lazily-evaluated [TileEntityType] instance.
     */
    val typeInstance: TileEntityType<*> by lazy {
        if (this.validBlocks.isEmpty())
            throw IncompleteBuilderException("Tile entity $registryName was never added to any blocks")
        val resolvedBlocks = this.validBlocks.map { it.get() }.toTypedArray()
        val type = TileEntityType.Builder.create(factory, *resolvedBlocks).build(null)
        type.registryName = registryName
        type
    }

    val lazy: LazyTileEntityType = LazyTileEntityType(this)
}
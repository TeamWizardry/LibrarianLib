package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import kotlin.reflect.KProperty

/**
 * A lazy access to a tile entity type. The result of adding a [TileEntitySpec] to a registration manager. Instances can
 * be created with a tile entity type directly if needed.
 */
public class LazyTileEntityType private constructor(
    @JvmSynthetic internal var typeInstance: (() -> TileEntityType<*>)?,
    @JvmSynthetic internal var spec: TileEntitySpec?
) {
    public constructor(spec: TileEntitySpec): this(spec::typeInstance, spec)
    public constructor(type: TileEntityType<*>): this({ type }, null)

    /**
     * Creates an empty tile entity type which can be configured later using [from]. This is useful for creating final
     * fields which can be populated later.
     */
    public constructor(): this(null, null)

    /**
     * Copies the other LazyTileEntityType into this LazyTileEntityType. This is useful for creating final fields which
     * can be populated later.
     */
    public fun from(other: LazyTileEntityType) {
        typeInstance = other.typeInstance
        spec = other.spec
    }

    /**
     * Get the tile entity type instance
     */
    public fun get(): TileEntityType<*> {
        return (typeInstance ?: throw IllegalStateException("LazyTileEntityType not initialized"))()
    }

    @JvmSynthetic
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): TileEntityType<*> {
        return get()
    }
}
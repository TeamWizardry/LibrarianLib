package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import kotlin.reflect.KProperty

/**
 * A lazy access to an entity type. The result of adding an [EntitySpec] to a registration manager. Instances can
 * be created with an entity type directly if needed.
 */
public class LazyEntityType<T: Entity> private constructor(
    @JvmSynthetic internal var typeInstance: (() -> EntityType<T>)?,
    @JvmSynthetic internal var spec: EntitySpec<T>?
) {
    public constructor(spec: EntitySpec<T>): this(spec::typeInstance, spec)
    public constructor(type: EntityType<T>): this({ type }, null)

    /**
     * Creates an empty entity type which can be configured later using [from]. This is useful for creating final
     * fields which can be populated later.
     */
    public constructor(): this(null, null)

    /**
     * Copies the other LazyEntityType into this LazyEntityType. This is useful for creating final fields which
     * can be populated later.
     */
    public fun from(other: LazyEntityType<T>) {
        typeInstance = other.typeInstance
        spec = other.spec
    }

    /**
     * Get the tile entity type instance
     */
    public fun get(): EntityType<T> {
        return (typeInstance ?: throw IllegalStateException("LazyTileEntityType not initialized"))()
    }

    @JvmSynthetic
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): EntityType<*> {
        return get()
    }
}
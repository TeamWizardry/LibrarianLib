package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.item.Item
import java.util.function.Supplier
import kotlin.reflect.KProperty

/**
 * A lazy access to an item instance. The result of adding an [ItemSpec] to a registration manager. Instances can be
 * created with an item directly if needed.
 */
public class LazyItem {
    private var itemInstance: Supplier<Item>?

    /**
     * Creates an empty item which can be configured later using [from]. This is useful for creating final fields which
     * can be populated later.
     */
    public constructor() {
        this.itemInstance = null
    }
    public constructor(itemInstance: Supplier<Item>) {
        this.itemInstance = itemInstance
    }
    public constructor(spec: ItemSpec): this(spec::itemInstance)
    public constructor(itemInstance: Item): this({ itemInstance })

    /**
     * Copies the other LazyItem into this LazyItem. This is useful for creating final fields which can be populated
     * later.
     */
    public fun from(other: LazyItem) {
        itemInstance = other.itemInstance
    }

    /**
     * Get the item instance
     */
    public fun get(): Item {
        return (itemInstance ?: throw IllegalStateException("LazyItem not initialized")).get()
    }

    @JvmSynthetic
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): Item {
        return get()
    }
}

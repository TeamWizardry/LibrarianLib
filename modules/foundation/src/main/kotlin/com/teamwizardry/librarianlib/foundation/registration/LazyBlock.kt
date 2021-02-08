package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.IItemProvider
import java.lang.IllegalStateException
import java.util.function.Supplier
import kotlin.reflect.KProperty

/**
 * A lazy access to a block instance and its item (if it has one). The result of adding a [BlockSpec] to a registration
 * manager. Instances can be created with a block directly if needed.
 */
public class LazyBlock : IItemProvider {
    private var blockInstance: Supplier<Block>?
    private var itemInstance: Supplier<Item?>?

    /**
     * Creates an empty block which can be configured later using [from]. This is useful for creating final fields which
     * can be populated later.
     */
    public constructor() {
        this.blockInstance = null
        this.itemInstance = null
    }
    public constructor(blockInstance: Supplier<Block>) {
        this.blockInstance = blockInstance
        this.itemInstance = null
    }
    public constructor(blockInstance: Supplier<Block>, itemInstance: Supplier<Item?>) {
        this.blockInstance = blockInstance
        this.itemInstance = itemInstance
    }
    public constructor(spec: BlockSpec): this(spec::blockInstance, spec::itemInstance)
    public constructor(blockInstance: Block): this({ blockInstance })
    public constructor(blockInstance: Block, itemInstance: Item?): this({ blockInstance }, { itemInstance })

    /**
     * Copies the other LazyBlock into this LazyBlock. This is useful for creating final fields which can be populated
     * later.
     */
    public fun from(other: LazyBlock) {
        blockInstance = other.blockInstance
        itemInstance = other.itemInstance
    }

    /**
     * Get the block instance
     */
    public fun get(): Block {
        return (blockInstance ?: throw IllegalStateException("LazyBlock not initialized")).get()
    }

    /**
     * Get the item instance, if it exists
     */
    public fun getItem(): Item? {
        return (itemInstance ?: throw IllegalStateException("LazyBlock not initialized")).get()
    }

    @JvmSynthetic
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): Block {
        return get()
    }

    override fun asItem(): Item {
        return getItem() ?: throw IllegalStateException("Tried to use an lazy block with no item as an IItemProvider")
    }
}
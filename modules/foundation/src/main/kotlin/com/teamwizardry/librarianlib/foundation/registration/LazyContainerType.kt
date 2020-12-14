package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.FacadeContainerType
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.ITextComponent
import kotlin.reflect.KProperty

/**
 * A lazy access to a container type. The result of adding a [ContainerSpec] to a registration manager.
 */
public class LazyContainerType<T: FacadeContainer> private constructor(private var containerType: (() -> FacadeContainerType<T>)?) {
    public constructor(spec: ContainerSpec<T>): this(spec::typeInstance)

    /**
     * Creates an empty container type which can be configured later using [from]. This is useful for creating final
     * fields which can be populated later.
     */
    public constructor(): this(null)

    /**
     * Copies the other LazyContainerType into this LazyContainerType. This is useful for creating final fields which
     * can be populated later.
     */
    public fun from(other: LazyContainerType<T>) {
        containerType = other.containerType
    }

    /**
     * A shortcut for calling [FacadeContainerType.open] on the type
     */
    public fun open(player: ServerPlayerEntity, title: ITextComponent, vararg arguments: Any?) {
        get().open(player, title, *arguments)
    }

    /**
     * Get the container type instance
     */
    public fun get(): FacadeContainerType<T> {
        return (containerType ?: throw IllegalStateException("LazyContainerType not initialized"))()
    }

    @JvmSynthetic
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): FacadeContainerType<T> {
        return get()
    }
}

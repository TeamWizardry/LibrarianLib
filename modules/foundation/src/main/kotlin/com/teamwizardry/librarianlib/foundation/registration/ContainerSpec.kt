package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.core.util.sided.ClientMetaSupplier
import com.teamwizardry.librarianlib.core.util.sided.ClientSideFunction
import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.container.FacadeContainerType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.Identifier
import net.minecraft.util.text.ITextComponent

/**
 * The specs for registering a container type.
 */
public class ContainerSpec<T: FacadeContainer>(
    /**
     * The registry name, sans mod ID
     */
    public var id: String,
    /**
     * The container class. This class must have a single constructor with entirely Prism-serializable arguments.
     */
    private val clazz: Class<T>,
    /**
     * The screen factory. This will generally be a lambda that returns a reference to the GUI's constructor
     */
    screenFactory: ClientMetaSupplier<ContainerScreenFactory<T>>
) {
    /**
     * The mod ID to register this container under. This is populated by the [RegistrationManager].
     */
    public var modid: String = ""
        @JvmSynthetic
        internal set

    /**
     * The registry name of the tile entity type. The [mod ID][modid] is populated by the [RegistrationManager].
     */
    public val registryName: Identifier
        get() = Identifier(modid, id)


    @get:JvmSynthetic
    internal var screenFactory: ClientMetaSupplier<ContainerScreenFactory<T>> = screenFactory
        private set

    public val typeInstance: FacadeContainerType<T> by lazy {
        val type = FacadeContainerType(clazz)
        type.registryName = registryName
        return@lazy type
    }

    public val lazy: LazyContainerType<T> = LazyContainerType(this)
}

public fun interface ContainerScreenFactory<T: FacadeContainer>: ClientSideFunction {
    public fun create(container: T, inventory: PlayerInventory, title: ITextComponent): FacadeContainerScreen<T>
}

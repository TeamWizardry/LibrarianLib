package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.foundation.capability.SimpleCapabilityStorage
import net.minecraftforge.common.capabilities.Capability
import java.util.concurrent.Callable

/**
 * The specs necessary for registering a capability.
 */
public class CapabilitySpec<T>(
    /**
     * The capability class. This is often an interface (e.g. forge's `IItemHandler`)
     */
    public val type: Class<T>,
    /**
     * The default storage type. For automatic storage, see [SimpleCapabilityStorage].
     */
    public val storage: Capability.IStorage<T>,
    /**
     * A supplier providing instances of the default implementation.
     */
    public val defaultImpl: Callable<T>
) {
}
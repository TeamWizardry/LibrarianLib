package com.teamwizardry.librarianlib.core.common;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @author WireSegal
 *         Created at 2:02 PM on 6/24/17.
 */
/*package-private*/ class RegistrationHandlerInternal {
    @SuppressWarnings("unchecked")
    /*package-private*/ static void registerGeneric(IForgeRegistry reg, IForgeRegistryEntry entry) {
        reg.register(entry);
    }
}

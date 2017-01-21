package com.teamwizardry.librarianlib.common.base.capability;

import net.minecraftforge.common.capabilities.Capability;

@FunctionalInterface
public interface ICapabilityObjectProvider<T> {
    Capability<T> invoke();
}

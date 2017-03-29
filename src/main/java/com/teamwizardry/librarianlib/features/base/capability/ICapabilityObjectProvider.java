package com.teamwizardry.librarianlib.features.base.capability;

import net.minecraftforge.common.capabilities.Capability;

/**
 * Usage:
 * As lambda: ()->cap
 * As anon class: new ICapabilityObjectProvider() {
 * return cap;
 * }
 * As method reference: Foo::getCap
 * As constructor reference: CapabilityFoo::new
 */
@FunctionalInterface
public interface ICapabilityObjectProvider<T> {
    Capability<T> invoke();
}

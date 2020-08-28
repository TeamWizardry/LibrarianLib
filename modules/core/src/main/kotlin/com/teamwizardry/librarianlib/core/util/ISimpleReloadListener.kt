package com.teamwizardry.librarianlib.core.util

import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager

public interface ISimpleReloadListener<T> {
    /**
     * Prepare for reloading on a background thread.
     * @return The value to pass to the [apply] method
     */
    public fun prepare(resourceManager: IResourceManager, profiler: IProfiler): T

    /**
     * Apply the reload on the main thread.
     * @param result The value returned from [prepare]
     */
    public fun apply(result: T, resourceManager: IResourceManager, profiler: IProfiler)
}

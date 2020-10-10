package com.teamwizardry.librarianlib.etcetera.eventbus

public abstract class Event {
    /**
     * Called before each event hook is called so the event can adjust itself based on the per-hook state persisted by
     * [storePerHookState].
     */
    protected open fun loadPerHookState(state: Any?) {

    }

    /**
     * Called after each event hook is called to persist any per-hook state necessary. The return value from this method
     * is passed to [loadPerHookState] the next time that particular event hook is called.
     */
    protected open fun storePerHookState(): Any? {
        return null
    }

    /**
     * Provides internal access for [EventBus].
     */
    @JvmSynthetic
    internal fun loadPerHookStateInternal(state: Any?) {
        loadPerHookState(state)
    }

    /**
     * Provides internal access for [EventBus].
     */
    @JvmSynthetic
    internal fun storePerHookStateInternal(): Any? {
        return storePerHookState()
    }
}

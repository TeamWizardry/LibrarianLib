package com.teamwizardry.librarianlib.utilities.eventbus

abstract class Event @JvmOverloads constructor(val reversed: Boolean = false) {
    /**
     * Persistent data stored in the event hook. Before each hook is called this is set to the currently stored hook
     * data, and after the event fires the current value is stored as the new hook data.
     */
    protected var hookData: Any? = null

    /**
     * Called before each hook is called and after [hookData] is set to allow this event to (re)initialize any state
     * it needs to.
     */
    protected open fun initializeHookState() {

    }

    /**
     * Called after each hook is called and before [hookData] is stored to allow this event to finalize anything it
     * needs to before being reset.
     */
    protected open fun finalizeHookState() {

    }

    /**
     * An internal delegate for [EventBus]
     */
    internal var hookDataInternal: Any?
        get() = hookData
        set(value) { hookData = value }

    /**
     * An internal delegate for [EventBus]
     */
    internal fun initializeHookStateInternal() {
        initializeHookState()
    }

    /**
     * An internal delegate for [EventBus]
     */
    internal fun finalizeHookStateInternal() {
        finalizeHookState()
    }
}

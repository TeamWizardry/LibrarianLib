package com.teamwizardry.librarianlib.etcetera.eventbus

public abstract class CancelableEvent : Event() {
    private var canceled: Boolean = false

    public fun isCanceled(): Boolean = canceled

    public fun cancel() {
        canceled = true
    }

}

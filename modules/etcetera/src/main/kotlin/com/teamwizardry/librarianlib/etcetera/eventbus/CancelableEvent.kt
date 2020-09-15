package com.teamwizardry.librarianlib.etcetera.eventbus

public abstract class CancelableEvent(reversed: Boolean = false) : Event(reversed) {
    private var canceled: Boolean = false

    public fun isCanceled(): Boolean = canceled

    public fun cancel() {
        canceled = true
    }

}

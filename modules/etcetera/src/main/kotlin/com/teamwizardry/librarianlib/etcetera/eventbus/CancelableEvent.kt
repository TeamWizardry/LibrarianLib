package com.teamwizardry.librarianlib.etcetera.eventbus

abstract class CancelableEvent(reversed: Boolean = false) : Event(reversed) {
    private var canceled: Boolean = false

    fun isCanceled() = canceled

    fun cancel() {
        canceled = true
    }

}

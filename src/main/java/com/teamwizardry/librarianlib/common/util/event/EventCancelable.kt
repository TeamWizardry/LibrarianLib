package com.teamwizardry.librarianlib.common.util.event

/**
 * Created by TheCodeWarrior
 */
abstract class EventCancelable(reversed: Boolean = false) : Event(reversed) {
    private var canceled: Boolean = false

    fun isCanceled() = canceled

    fun cancel() {
        canceled = true
    }

}

package com.teamwizardry.librarianlib.features.eventbus

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

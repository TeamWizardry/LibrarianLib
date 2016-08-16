package com.teamwizardry.librarianlib.util.event

/**
 * Created by TheCodeWarrior
 */
abstract class EventCancelable(reversed: Boolean = false) : Event(reversed) {
    private var canceled: Boolean = false

    fun isCanceled() : Boolean{
        return canceled
    }

    fun cancel() {
        canceled = true;
    }

}
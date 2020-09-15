package com.teamwizardry.librarianlib.facade.layer

public interface AnimationTimeListener {
    /**
     * Updates the current time. The passed time is expressed in ticks since some arbitrary fixed time in the past.
     */
    public fun updateTime(time: Float)
}
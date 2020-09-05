package com.teamwizardry.librarianlib.facade.pastry.components

import com.teamwizardry.librarianlib.etcetera.eventbus.EventBus
import com.teamwizardry.librarianlib.etcetera.eventbus.CancelableEvent
import com.teamwizardry.librarianlib.facade.pastry.ExperimentalPastryAPI

@ExperimentalPastryAPI
public class PastryRadioButtonSet<T> {
    @JvmField
    public val BUS: EventBus = EventBus()

    public inner class OptionSelected(public val option: T?): CancelableEvent()

    private val components = mutableMapOf<T, PastryCheckbox>()

    public var selectedValue: T? = null
        private set(value) {
            if (field != value) {
                field = value
                components.forEach { key, box ->
                    box.state = key == value
                }
            }
        }

    public fun addOption(option: T, x: Int, y: Int): PastryCheckbox {
        val checkbox = PastryCheckbox(x, y, true)
        components[option] = checkbox
        checkbox.BUS.hook<PastryToggle.BeginToggleEvent> {
            if (checkbox.state) it.cancel()
        }
        checkbox.BUS.hook<PastryToggle.StateWillChangeEvent> {
            if (!BUS.fire(OptionSelected(option)).isCanceled()) {
                selectedValue = option
            }
            it.cancel()
        }
        return checkbox
    }
}
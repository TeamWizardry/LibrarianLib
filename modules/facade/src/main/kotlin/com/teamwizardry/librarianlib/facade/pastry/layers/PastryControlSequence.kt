package com.teamwizardry.librarianlib.facade.pastry.layers

public class PastryControlSequence {
    private var head: PastryControl? = null

    public fun <T: PastryControl> add(control: T): T {
        head?.next = control
        control.previous = head
        head = control
        return control
    }
}
package com.teamwizardry.librarianlib.etcetera.example.eventbus;

import com.teamwizardry.librarianlib.etcetera.eventbus.CancelableEvent;

public class ExampleCancelableEvent extends CancelableEvent {
    public final int action;

    public ExampleCancelableEvent(int action) {
        this.action = action;
    }
}

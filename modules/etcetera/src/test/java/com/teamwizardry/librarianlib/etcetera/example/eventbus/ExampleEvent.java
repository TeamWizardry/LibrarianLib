package com.teamwizardry.librarianlib.etcetera.example.eventbus;

import com.teamwizardry.librarianlib.etcetera.eventbus.Event;

public class ExampleEvent extends Event {
    public final int action;

    public ExampleEvent(int action) {
        this.action = action;
    }
}

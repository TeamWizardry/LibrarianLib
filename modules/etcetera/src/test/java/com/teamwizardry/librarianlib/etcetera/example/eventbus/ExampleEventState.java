package com.teamwizardry.librarianlib.etcetera.example.eventbus;

import com.teamwizardry.librarianlib.etcetera.eventbus.Event;
import org.jetbrains.annotations.Nullable;

public class ExampleEventState extends Event {
    public final int delta;
    public int accumulator;

    public ExampleEventState(int delta) {
        this.delta = delta;
    }

    @Override
    protected void loadPerHookState(@Nullable Object state) {
        if(state != null) {
            accumulator = (Integer)state;
        } else {
            accumulator = 0;
        }
        accumulator += delta;
    }

    @Nullable
    @Override
    protected Object storePerHookState() {
        return accumulator;
    }
}

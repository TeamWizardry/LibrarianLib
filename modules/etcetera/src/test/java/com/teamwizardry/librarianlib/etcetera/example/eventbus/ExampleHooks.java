package com.teamwizardry.librarianlib.etcetera.example.eventbus;

import com.teamwizardry.librarianlib.etcetera.eventbus.EventBus;
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook;

public class ExampleHooks {
    private EventBus BUS = new EventBus();

    public void manualHook() {
        BUS.hook(ExampleEvent.class, (ExampleEvent e) -> {
            // run code here
        });
    }

    @Hook
    private void example(ExampleEvent e) {
        // run code here
    }

    public void autoHook() {
        BUS.register(this);
    }

}

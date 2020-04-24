package com.teamwizardry.librarianlib.facade.value;

import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("Duplicates")
public class RMValueBoolean {
    private boolean value;
    @Nullable
    private final ChangeListener.Boolean change;

    public RMValueBoolean(boolean initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueBoolean(boolean initialValue, @NotNull ChangeListener.Boolean change) {
        this.value = initialValue;
        this.change = change;
    }

    /**
     * Gets the current value
     */
    public boolean get() {
        return value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
     */
    public void set(boolean value) {
        this.value = value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public boolean getValue(Object thisRef, KProperty property) {
        return value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty property, boolean value) {
        set(value);
    }
}

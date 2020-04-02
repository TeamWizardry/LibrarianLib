package com.teamwizardry.librarianlib.gui.value;

import com.teamwizardry.librarianlib.math.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class RMValueInt {
    private int value;
    @Nullable
    private final ChangeListener.Int change;

    public RMValueInt(int initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueInt(int initialValue, @NotNull ChangeListener.Int change) {
        this.value = initialValue;
        this.change = change;
    }

    /**
     * Gets the current value
     */
    public int get() {
        return value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
     */
    public void set(int value) {
        this.value = value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public int getValue(Object thisRef, KProperty property) {
        return value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty property, int value) {
        set(value);
    }
}

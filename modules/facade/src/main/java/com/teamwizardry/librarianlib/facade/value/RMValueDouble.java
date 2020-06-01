package com.teamwizardry.librarianlib.facade.value;

import com.teamwizardry.librarianlib.math.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class RMValueDouble extends GuiValue<Double> {
    private double value;
    @Nullable
    private final ChangeListener.Double change;

    public RMValueDouble(double initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueDouble(double initialValue, @Nullable ChangeListener.Double change) {
        this.value = initialValue;
        this.change = change;
    }

    /**
     * Gets the current value
     */
    public double get() {
        return value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
     */
    public void set(double value) {
        this.value = value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public double getValue(Object thisRef, KProperty property) {
        return value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty property, double value) {
        set(value);
    }

    @Override
    protected boolean getHasLerper() {
        return true;
    }

    @Override
    protected Double getCurrentValue() {
        return get();
    }

    @Override
    protected Double lerp(Double from, Double to, float fraction) {
        return from + (to - from) * fraction;
    }

    @Override
    protected void animationChange(Double from, Double to) {
        if(change != null)
            change.report(from, to);
    }

    @Override
    protected void persistAnimation(Double value) {
        set(value);
    }
}

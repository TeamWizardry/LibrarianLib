package com.teamwizardry.librarianlib.facade.value;

import com.teamwizardry.librarianlib.math.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class RMValueInt extends GuiValue<Integer> {
    private int value;
    @Nullable
    private final ChangeListener.Int change;

    public RMValueInt(int initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueInt(int initialValue, @Nullable ChangeListener.Int change) {
        this.value = initialValue;
        this.change = change;
    }

    /**
     * Gets the current value
     */
    public int get() {
        return getUseAnimationValue() ? getAnimationValue() : value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
     */
    public void set(int value) {
        int oldValue = this.value;
        this.value = value;
        if (oldValue != value && change != null && !getUseAnimationValue()) {
            change.report(oldValue, value);
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public int getValue(Object thisRef, KProperty<?> property) {
        return this.get();
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty<?> property, int value) {
        this.set(value);
    }

    @Override
    protected boolean getHasLerper() {
        return true;
    }

    @Override
    protected Integer getCurrentValue() {
        return get();
    }

    @Override
    protected Integer lerp(Integer from, Integer to, float fraction) {
        return (int) (from + (to - from) * fraction);
    }

    @Override
    protected void animationChange(Integer from, Integer to) {
        if (change != null)
            change.report(from, to);
    }

    @Override
    protected void persistAnimation(Integer value) {
        this.value = value;
    }
}

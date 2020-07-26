package com.teamwizardry.librarianlib.facade.value;

import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("Duplicates")
public class RMValueBoolean extends GuiValue<Boolean> {
    private boolean value;
    @Nullable
    private final ChangeListener.Boolean change;

    public RMValueBoolean(boolean initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueBoolean(boolean initialValue, @Nullable ChangeListener.Boolean change) {
        this.value = initialValue;
        this.change = change;
    }

    /**
     * Gets the current value
     */
    public boolean get() {
        return getUseAnimationValue() ? getAnimationValue() : value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
     */
    public void set(boolean value) {
        boolean oldValue = this.value;
        this.value = value;
        if(oldValue != value && change != null && !getUseAnimationValue()) {
            change.report(oldValue, value);
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public boolean getValue(Object thisRef, KProperty<?> property) {
        return this.get();
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty<?> property, boolean value) {
        this.set(value);
    }

    @Override
    protected boolean getHasLerper() {
        return true;
    }

    @Override
    protected Boolean getCurrentValue() {
        return get();
    }

    @Override
    protected Boolean lerp(Boolean from, Boolean to, float fraction) {
        return fraction < 0.5f ? from : to;
    }

    @Override
    protected void animationChange(Boolean from, Boolean to) {
        if(change != null)
            change.report(from, to);
    }

    @Override
    protected void persistAnimation(Boolean value) {
        this.value = value;
    }
}

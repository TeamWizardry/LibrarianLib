package com.teamwizardry.librarianlib.facade.value;

import kotlin.reflect.KProperty;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

@SuppressWarnings("Duplicates")
public class IMValueBoolean extends GuiValue<Boolean> {
    private Storage storage;

    private IMValueBoolean(Storage initialStorage) {
        this.storage = initialStorage;
    }

    public IMValueBoolean(boolean initialValue) {
        this.storage = new Storage.Fixed(initialValue);
    }

    public IMValueBoolean(BooleanSupplier initialCallback) {
        this.storage = new Storage.Callback(initialCallback);
    }

    /**
     * Gets the current value
     */
    public boolean get() {
        return getUseAnimationValue() ? getAnimationValue() : storage.get();
    }

    /**
     * Sets the callback, unsetting the fixed value in the process
     */
    public void set(BooleanSupplier callback) {
        if (storage instanceof Storage.Callback) {
            ((Storage.Callback) storage).callback = callback;
        } else {
            storage = new Storage.Callback(callback);
        }
    }

    /**
     * Sets the fixed callback. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call booleano `somePropery_im` for its value)
     */
    public void setValue(boolean value) {
        if (storage instanceof Storage.Fixed) {
            ((Storage.Fixed) storage).value = value;
        } else {
            storage = new Storage.Fixed(value);
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public boolean getValue(Object thisRef, KProperty<?> property) {
        return this.get();
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public void setValue(Object thisRef, KProperty<?> property, boolean value) {
        setValue(value);
    }

    /**
     * Gets the current callback, or null if this IMValueBoolean has a fixed value
     */
    @Nullable
    public BooleanSupplier getCallback() {
        if (storage instanceof Storage.Callback) {
            return ((Storage.Callback) storage).callback;
        } else {
            return null;
        }
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
        // nop
    }

    @Override
    protected void persistAnimation(Boolean value) {
        setValue(value);
    }

    private static abstract class Storage {
        abstract boolean get();

        static class Fixed extends IMValueBoolean.Storage {
            boolean value;

            public Fixed(boolean value) {
                this.value = value;
            }

            @Override
            boolean get() {
                return value;
            }
        }

        static class Callback extends IMValueBoolean.Storage {
            BooleanSupplier callback;

            public Callback(BooleanSupplier callback) {
                this.callback = callback;
            }

            @Override
            boolean get() {
                return callback.getAsBoolean();
            }
        }
    }
}

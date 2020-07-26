package com.teamwizardry.librarianlib.facade.value;

import kotlin.reflect.KProperty;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;

@SuppressWarnings("Duplicates")
public class IMValueDouble extends GuiValue<Double> {
    private Storage storage;

    private IMValueDouble(Storage initialStorage) {
        this.storage = initialStorage;
    }
    public IMValueDouble(double initialValue) {
        this.storage = new Storage.Fixed(initialValue);
    }
    public IMValueDouble(DoubleSupplier initialCallback) {
        this.storage = new Storage.Callback(initialCallback);
    }

    /**
     * Gets the current value
     */
    public double get() {
        return getUseAnimationValue() ? getAnimationValue() : storage.get();
    }

    /**
     * Sets the callback, unsetting the fixed value in the process
     */
    public void set(DoubleSupplier callback) {
        if(storage instanceof Storage.Callback) {
            ((Storage.Callback) storage).callback = callback;
        } else {
            storage = new Storage.Callback(callback);
        }
    }

    /**
     * Sets the fixed callback. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call doubleo `somePropery_im` for its value)
     */
    public void setValue(double value) {
        if(storage instanceof Storage.Fixed) {
            ((Storage.Fixed) storage).value = value;
        } else {
            storage = new Storage.Fixed(value);
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public double getValue(Object thisRef, KProperty<?> property) {
        return this.get();
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public void setValue(Object thisRef, KProperty<?> property, double value) {
        setValue(value);
    }

    /**
     * Gets the current callback, or null if this IMValueDouble has a fixed value
     */
    @Nullable
    public DoubleSupplier getCallback() {
        if(storage instanceof Storage.Callback) {
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
    protected Double getCurrentValue() {
        return get();
    }

    @Override
    protected Double lerp(Double from, Double to, float fraction) {
        return from + (to - from) * fraction;
    }

    @Override
    protected void animationChange(Double from, Double to) {
        // nop
    }

    @Override
    protected void persistAnimation(Double value) {
        setValue(value);
    }

    private static abstract class Storage {
        abstract double get();

        static class Fixed extends IMValueDouble.Storage {
            double value;
            public Fixed(double value) {
                this.value = value;
            }

            @Override
            double get() {
                return value;
            }
        }

        static class Callback extends IMValueDouble.Storage {
            DoubleSupplier callback;
            public Callback(DoubleSupplier callback) {
                this.callback = callback;
            }

            @Override
            double get() {
                return callback.getAsDouble();
            }
        }
    }
}

package com.teamwizardry.librarianlib.facade.value;

import com.teamwizardry.librarianlib.math.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

@SuppressWarnings("Duplicates")
public class IMValueInt extends GuiValue<Integer> {
    private Storage storage;

    private IMValueInt(Storage initialStorage) {
        this.storage = initialStorage;
    }

    public IMValueInt(int initialValue) {
        this.storage = new Storage.Fixed(initialValue);
    }

    public IMValueInt(IntSupplier initialCallback) {
        this.storage = new Storage.Callback(initialCallback);
    }

    /**
     * Gets the current value
     */
    public int get() {
        return getUseAnimationValue() ? getAnimationValue() : storage.get();
    }

    /**
     * Sets the callback, unsetting the fixed value in the process
     */
    public void set(IntSupplier callback) {
        if (storage instanceof Storage.Callback) {
            ((Storage.Callback) storage).callback = callback;
        } else {
            storage = new Storage.Callback(callback);
        }
    }

    /**
     * Sets the fixed callback. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_im` for its value)
     */
    public void setValue(int value) {
        if (storage instanceof Storage.Fixed) {
            ((Storage.Fixed) storage).value = value;
        } else {
            storage = new Storage.Fixed(value);
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public int getValue(Object thisRef, KProperty<?> property) {
        return this.get();
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public void setValue(Object thisRef, KProperty<?> property, int value) {
        setValue(value);
    }

    /**
     * Gets the current callback, or null if this IMValueInt has a fixed value
     */
    @Nullable
    public IntSupplier getCallback() {
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
    protected Integer getCurrentValue() {
        return get();
    }

    @Override
    protected Integer lerp(Integer from, Integer to, float fraction) {
        return (int) (from + (to - from) * fraction);
    }

    @Override
    protected void animationChange(Integer from, Integer to) {
        // nop
    }

    @Override
    protected void persistAnimation(Integer value) {
        setValue(value);
    }

    private static abstract class Storage {
        abstract int get();

        static class Fixed extends Storage {
            int value;

            public Fixed(int value) {
                this.value = value;
            }

            @Override
            int get() {
                return value;
            }
        }

        static class Callback extends Storage {
            IntSupplier callback;

            public Callback(IntSupplier callback) {
                this.callback = callback;
            }

            @Override
            int get() {
                return callback.getAsInt();
            }
        }
    }
}

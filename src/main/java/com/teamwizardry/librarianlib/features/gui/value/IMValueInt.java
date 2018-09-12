package com.teamwizardry.librarianlib.features.gui.value;

import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntSupplier;

public class IMValueInt implements GuiAnimatable {
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
        return storage.get();
    }

    /**
     * Sets the callback, unsetting the fixed value in the process
     */
    public void set(IntSupplier callback) {
        GuiAnimator.getCurrent().add(this);
        if(storage instanceof Storage.Callback) {
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
        GuiAnimator.getCurrent().add(this);
        if(storage instanceof Storage.Fixed) {
            ((Storage.Fixed) storage).value = value;
        } else {
            storage = new Storage.Fixed(value);
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public int getValue(Object thisRef, KProperty property) {
        return storage.get();
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public void setValue(Object thisRef, KProperty property, int value) {
        setValue(value);
    }

    /**
     * A kotlin helper to allow cleanly specifying the callback (`something.theValue_im { return someValue }`)
     */
    public void invoke(IntSupplier callback) {
        set(callback);
    }

    /**
     * Gets the current callback, or null if this IMValueInt has a fixed value
     */
    @Nullable
    public IntSupplier getCallback() {
        if(storage instanceof Storage.Callback) {
            return ((Storage.Callback) storage).callback;
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public Object getAnimatableValue() {
        return this.get();
    }

    @Override
    public void setAnimatableValue(@Nullable Object value) {
        this.setValue((int) value);
    }

    @Nullable
    @Override
    public Object getAnimatableCallback() {
        return this.getCallback();
    }

    @Override
    public void setAnimatableCallback(@NotNull Object supplier) {
        this.set((IntSupplier) supplier);
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

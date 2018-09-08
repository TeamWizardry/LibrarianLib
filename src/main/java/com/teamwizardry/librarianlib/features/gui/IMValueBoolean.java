package com.teamwizardry.librarianlib.features.gui;

import kotlin.reflect.KProperty;

import java.util.function.BooleanSupplier;

public class IMValueBoolean {
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
        return storage.get();
    }

    /**
     * Sets the callback, unsetting the fixed value in the process
     */
    public void set(BooleanSupplier callback) {
        if(storage instanceof Storage.Callback) {
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
        if(storage instanceof Storage.Fixed) {
            ((Storage.Fixed) storage).value = value;
        } else {
            storage = new Storage.Fixed(value);
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public boolean getValue(Object thisRef, KProperty property) {
        return storage.get();
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public void setValue(Object thisRef, KProperty property, boolean value) {
        setValue(value);
    }

    /**
     * A kotlin helper to allow cleanly specifying the callback (`something.theValue_im { return someValue }`)
     */
    public void invoke(BooleanSupplier callback) {
        set(callback);
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

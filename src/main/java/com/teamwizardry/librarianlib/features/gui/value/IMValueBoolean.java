package com.teamwizardry.librarianlib.features.gui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

@SuppressWarnings("Duplicates")
public class IMValueBoolean implements GuiAnimatable {
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
        GuiAnimator.getCurrent().add(this);
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

    /**
     * Gets the current callback, or null if this IMValueBoolean has a fixed value
     */
    @Nullable
    public BooleanSupplier getCallback() {
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
        this.setValue((boolean) value);
    }

    @Nullable
    @Override
    public Object getAnimatableCallback() {
        return this.getCallback();
    }

    @Override
    public void setAnimatableCallback(@NotNull Object supplier) {
        this.set((BooleanSupplier) supplier);
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

    public Animation<IMValueBoolean> animate(boolean to, float delay) {
        AnimationImpl anim = (AnimationImpl) animate(get(), to, delay);
        anim.implicitStart = true;
        return anim;
    }
    public Animation<IMValueBoolean> animate(boolean from, boolean to, float delay) {
        AnimationImpl animation = new AnimationImpl(from, to, this);
        animation.setDuration(delay);
        Animator.global.add(animation);
        return animation;
    }

    private class AnimationImpl extends Animation<IMValueBoolean> {
        boolean from, to;
        boolean implicitStart;

        AnimationImpl(boolean from, boolean to, IMValueBoolean target) {
            super(target);
            this.from = from;
            this.to = to;
        }

        public void update(float time) {
            if(implicitStart) {
                from = getTarget().get();
                implicitStart = false;
            }
            getTarget().setValue(time == 1 ? to : from);
        }
    }
}

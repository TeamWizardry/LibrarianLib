package com.teamwizardry.librarianlib.features.gui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.LongSupplier;

public class IMValueLong implements GuiAnimatable {
    private Storage storage;

    private IMValueLong(Storage initialStorage) {
        this.storage = initialStorage;
    }
    public IMValueLong(long initialValue) {
        this.storage = new Storage.Fixed(initialValue);
    }
    public IMValueLong(LongSupplier initialCallback) {
        this.storage = new Storage.Callback(initialCallback);
    }

    /**
     * Gets the current value
     */
    public long get() {
        return storage.get();
    }

    /**
     * Sets the callback, unsetting the fixed value in the process
     */
    public void set(LongSupplier callback) {
        GuiAnimator.getCurrent().add(this);
        if(storage instanceof Storage.Callback) {
            ((Storage.Callback) storage).callback = callback;
        } else {
            storage = new Storage.Callback(callback);
        }
    }

    /**
     * Sets the fixed callback. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call longo `somePropery_im` for its value)
     */
    public void setValue(long value) {
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
    public long getValue(Object thisRef, KProperty property) {
        return storage.get();
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public void setValue(Object thisRef, KProperty property, long value) {
        setValue(value);
    }

    /**
     * A kotlin helper to allow cleanly specifying the callback (`something.theValue_im { return someValue }`)
     */
    public void invoke(LongSupplier callback) {
        set(callback);
    }

    /**
     * Gets the current callback, or null if this IMValueLong has a fixed value
     */
    @Nullable
    public LongSupplier getCallback() {
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
        this.setValue((long) value);
    }

    @Nullable
    @Override
    public Object getAnimatableCallback() {
        return this.getCallback();
    }

    @Override
    public void setAnimatableCallback(@NotNull Object supplier) {
        this.set((LongSupplier) supplier);
    }

    private static abstract class Storage {
        abstract long get();

        static class Fixed extends IMValueLong.Storage {
            long value;
            public Fixed(long value) {
                this.value = value;
            }

            @Override
            long get() {
                return value;
            }
        }

        static class Callback extends IMValueLong.Storage {
            LongSupplier callback;
            public Callback(LongSupplier callback) {
                this.callback = callback;
            }

            @Override
            long get() {
                return callback.getAsLong();
            }
        }
    }

    public void animate(long from, long to, float duration, Easing easing) { animate(from, to, duration, easing, 0); }
    public void animate(long from, long to, float duration) { animate(from, to, duration, Easing.linear, 0); }

    public void animate(long from, long to, float duration, Easing easing, float delay) {
        AnimationImpl animation = new AnimationImpl(from, to, this);
        animation.setDuration(duration);
        animation.easing = easing;
        animation.setStart(delay);
        Animator.global.add(animation);
    }

    public void animate(long to, float duration) { animate(to, duration, Easing.linear, 0); }
    public void animate(long to, float duration, Easing easing) { animate(to, duration, easing, 0); }

    public void animate(long to, float duration, Easing easing, float delay) {
        AnimationImpl animation = new AnimationImpl(get(), to, this);
        animation.implicitStart = true;
        animation.setDuration(duration);
        animation.easing = easing;
        animation.setStart(delay);
        Animator.global.add(animation);
    }

    private class AnimationImpl extends Animation<IMValueLong> {
        long from, to;
        boolean implicitStart;

        AnimationImpl(long from, long to, IMValueLong target) {
            super(target);
            this.from = from;
            this.to = to;
        }
        Easing easing = Easing.linear;

        public void update(float time) {
            if(implicitStart) {
                from = getTarget().get();
                implicitStart = false;
            }
            float progress = easing.invoke(timeFraction(time));
            long newValue = (long)(from + (to-from) * progress);
            getTarget().setValue(newValue);
        }
    }
}

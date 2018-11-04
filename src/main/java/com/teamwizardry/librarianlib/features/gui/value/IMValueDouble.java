package com.teamwizardry.librarianlib.features.gui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;

public class IMValueDouble implements GuiAnimatable {
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
        return storage.get();
    }

    /**
     * Sets the callback, unsetting the fixed value in the process
     */
    public void set(DoubleSupplier callback) {
        GuiAnimator.getCurrent().add(this);
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
    public double getValue(Object thisRef, KProperty property) {
        return storage.get();
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    public void setValue(Object thisRef, KProperty property, double value) {
        setValue(value);
    }

    /**
     * A kotlin helper to allow cleanly specifying the callback (`something.theValue_im { return someValue }`)
     */
    public void invoke(DoubleSupplier callback) {
        set(callback);
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

    @Nullable
    @Override
    public Object getAnimatableValue() {
        return this.get();
    }

    @Override
    public void setAnimatableValue(@Nullable Object value) {
        this.setValue((double) value);
    }

    @Nullable
    @Override
    public Object getAnimatableCallback() {
        return this.getCallback();
    }

    @Override
    public void setAnimatableCallback(@NotNull Object supplier) {
        this.set((DoubleSupplier) supplier);
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

    public Animation<IMValueDouble> animate(double from, double to, float duration, Easing easing, float delay) {
        AnimationImpl animation = new AnimationImpl(from, to, this);
        animation.setDuration(duration);
        animation.easing = easing;
        animation.setStart(delay);
        Animator.global.add(animation);
        return animation;
    }

    public Animation<IMValueDouble> animate(double from, double to, float duration, Easing easing) {
        return animate(from, to, duration, easing, 0);
    }
    public Animation<IMValueDouble> animate(double from, double to, float duration) {
        return animate(from, to, duration, Easing.linear, 0);
    }

    public Animation<IMValueDouble> animate(double to, float duration, Easing easing, float delay) {
        AnimationImpl anim = (AnimationImpl) animate(get(), to, duration, easing, delay);
        anim.implicitStart = true;
        return anim;
    }
    public Animation<IMValueDouble> animate(double to, float duration) {
        return animate(to, duration, Easing.linear, 0);
    }
    public Animation<IMValueDouble> animate(double to, float duration, Easing easing) {
        return animate(to, duration, easing, 0);
    }

    private class AnimationImpl extends Animation<IMValueDouble> {
        double from, to;
        boolean implicitStart;

        AnimationImpl(double from, double to, IMValueDouble target) {
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
            double newValue = from + (to-from) * progress;
            getTarget().setValue(newValue);
        }
    }
}

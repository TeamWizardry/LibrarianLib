package com.teamwizardry.librarianlib.gui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.NullAnimatable;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

@SuppressWarnings("Duplicates")
public class IMValueDouble implements GuiAnimatable<IMValueDouble> {
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
            super(target, new NullAnimatable<>());
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

    public KeyframeAnimationBuilder animateKeyframes(double initialValue) {
        return animateKeyframes(initialValue, 0f);
    }
    public KeyframeAnimationBuilder animateKeyframes(double initialValue, float delay) {
        return new KeyframeAnimationBuilder(initialValue, delay, this);
    }

    public static class KeyframeAnimationBuilder {
        private float delay;
        private IMValueDouble target;
        private List<Keyframe> keyframes;

        KeyframeAnimationBuilder(double initialValue, float delay, IMValueDouble target) {
            this.keyframes = new ArrayList<>();
            this.target = target;
            this.delay = delay;
            keyframes.add(new Keyframe(0f, initialValue, Easing.linear));
        }

        /**
         * Add a keyframe [time] ticks after the previous one
         */
        public @NotNull KeyframeAnimationBuilder add(float time, double value) {
            return add(time, value, Easing.linear);
        }

        /**
         * Add a keyframe [time] ticks after the previous one
         */
        public @NotNull KeyframeAnimationBuilder add(float time, double value, @NotNull Easing easing) {
            keyframes.add(new Keyframe(time, value, easing));
            return this;
        }

        public @NotNull Animation<IMValueDouble> finish() {
            if(keyframes.isEmpty()) throw new IllegalStateException("Cannot create an empty keyframe animation");

            float duration = 0f;
            for (Keyframe it : keyframes) {
                duration += it.time;
            }
            float total = 0f;
            for (Keyframe it : keyframes) {
                total += it.time;
                it.time = total / duration;
            }

            Animation<IMValueDouble> animation = new KeyframeAnimation(target, keyframes);
            animation.setDuration(duration);
            animation.setStart(delay);
            Animator.global.add(animation);
            return animation;
        }
    }

    private static class Keyframe {
        float time;
        double value;
        Easing easing;

        Keyframe(float time, double value, Easing easing) {
            this.time = time;
            this.value = value;
            this.easing = easing;
        }
    }

    private static class KeyframeAnimation extends Animation<IMValueDouble> {
        private List<Keyframe> keyframes;

        KeyframeAnimation(IMValueDouble target, List<Keyframe> keyframes) {
            super(target, new NullAnimatable<>());
            this.keyframes = keyframes;
        }

        @Override
        public void update(float time) {
            float progress = timeFraction(time);
            Keyframe prev = null, next = null;
            for (Keyframe it: keyframes) {
                if(it.time <= progress) prev = it;
                if(it.time >= progress && next == null) next = it;
            }
            if (prev != null && next != null) {
                if (next.time == prev.time) { // this can only happen with single-keyframe animations or when we are on top of a keyframe
                    getTarget().setValue(next.value);
                } else {
                    float partialProgress = next.easing.invoke((progress - prev.time) / (next.time - prev.time));
                    getTarget().setValue(prev.value + (next.value-prev.value) * partialProgress);
                }
            } else if (next != null) {
                getTarget().setValue(next.value);
            } else if (prev != null) {
                getTarget().setValue(prev.value);
            }

        }
    }
}

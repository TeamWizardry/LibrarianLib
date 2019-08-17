package com.teamwizardry.librarianlib.features.facade.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.NullAnimatable;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

@SuppressWarnings("Duplicates")
public class IMValueLong implements GuiAnimatable<IMValueLong> {
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

    public Animation<IMValueLong> animate(long from, long to, float duration, Easing easing, float delay) {
        AnimationImpl animation = new AnimationImpl(from, to, this);
        animation.setDuration(duration);
        animation.easing = easing;
        animation.setStart(delay);
        Animator.global.add(animation);
        return animation;
    }

    public Animation<IMValueLong> animate(long from, long to, float duration, Easing easing) {
        return animate(from, to, duration, easing, 0);
    }
    public Animation<IMValueLong> animate(long from, long to, float duration) {
        return animate(from, to, duration, Easing.linear, 0);
    }

    public Animation<IMValueLong> animate(long to, float duration, Easing easing, float delay) {
        AnimationImpl anim = (AnimationImpl) animate(get(), to, duration, easing, delay);
        anim.implicitStart = true;
        return anim;
    }
    public Animation<IMValueLong> animate(long to, float duration) {
        return animate(to, duration, Easing.linear, 0);
    }
    public Animation<IMValueLong> animate(long to, float duration, Easing easing) {
        return animate(to, duration, easing, 0);
    }

    private class AnimationImpl extends Animation<IMValueLong> {
        long from, to;
        boolean implicitStart;

        AnimationImpl(long from, long to, IMValueLong target) {
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
            long newValue = (long)(from + (to-from) * progress);
            getTarget().setValue(newValue);
        }
    }

    public KeyframeAnimationBuilder animateKeyframes(long initialValue) {
        return animateKeyframes(initialValue, 0f);
    }
    public KeyframeAnimationBuilder animateKeyframes(long initialValue, float delay) {
        return new KeyframeAnimationBuilder(initialValue, delay, this);
    }

    public static class KeyframeAnimationBuilder {
        private float delay;
        private IMValueLong target;
        private List<Keyframe> keyframes;

        KeyframeAnimationBuilder(long initialValue, float delay, IMValueLong target) {
            this.keyframes = new ArrayList<>();
            this.target = target;
            this.delay = delay;
            keyframes.add(new Keyframe(0f, initialValue, Easing.linear));
        }

        /**
         * Add a keyframe [time] ticks after the previous one
         */
        public @NotNull KeyframeAnimationBuilder add(float time, long value) {
            return add(time, value, Easing.linear);
        }

        /**
         * Add a keyframe [time] ticks after the previous one
         */
        public @NotNull KeyframeAnimationBuilder add(float time, long value, @NotNull Easing easing) {
            keyframes.add(new Keyframe(time, value, easing));
            return this;
        }

        public @NotNull Animation<IMValueLong> finish() {
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

            Animation<IMValueLong> animation = new KeyframeAnimation(target, keyframes);
            animation.setDuration(duration);
            animation.setStart(delay);
            Animator.global.add(animation);
            return animation;
        }
    }

    private static class Keyframe {
        float time;
        long value;
        Easing easing;

        Keyframe(float time, long value, Easing easing) {
            this.time = time;
            this.value = value;
            this.easing = easing;
        }
    }

    private static class KeyframeAnimation extends Animation<IMValueLong> {
        private List<Keyframe> keyframes;

        KeyframeAnimation(IMValueLong target, List<Keyframe> keyframes) {
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
                    getTarget().setValue((int)(prev.value + (next.value-prev.value) * partialProgress));
                }
            } else if (next != null) {
                getTarget().setValue(next.value);
            } else if (prev != null) {
                getTarget().setValue(prev.value);
            }

        }
    }
}

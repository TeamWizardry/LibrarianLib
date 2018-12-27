package com.teamwizardry.librarianlib.features.gui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class RMValueLong {
    private long value;
    @Nullable
    private final ChangeListener.Long change;

    public RMValueLong(long initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueLong(long initialValue, @NotNull ChangeListener.Long change) {
        this.value = initialValue;
        this.change = change;
    }

    /**
     * Gets the current array
     */
    public long get() {
        return value;
    }

    /**
     * Sets the array. This isn't often called as most classes will provide a delegated property to directly
     * access this array (`someProperty` will call into `somePropery_rm` for its array)
     */
    public void set(long value) {
        GuiAnimator.getCurrent().add(animatable);
        this.value = value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public long getValue(Object thisRef, KProperty property) {
        return value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty property, long value) {
        set(value);
    }

    private Animatable animatable = new Animatable();
    private class Animatable implements GuiAnimatable {
        @Nullable
        @Override
        public Object getAnimatableValue() {
            return get();
        }

        @Override
        public void setAnimatableValue(@Nullable Object value) {
            set((long) value);
        }

        @Nullable
        @Override
        public Object getAnimatableCallback() {
            return null;
        }

        @Override
        public void setAnimatableCallback(@NotNull Object supplier) {
            //nop
        }
    }

    public Animation<RMValueLong> animate(long from, long to, float duration, Easing easing) {
        return animate(from, to, duration, easing, 0);
    }
    public Animation<RMValueLong> animate(long from, long to, float duration) {
        return animate(from, to, duration, Easing.linear, 0);
    }

    public Animation<RMValueLong> animate(long from, long to, float duration, Easing easing, float delay) {
        AnimationImpl animation = new AnimationImpl(from, to, this);
        animation.setDuration(duration);
        animation.easing = easing;
        animation.setStart(delay);
        Animator.global.add(animation);
        return animation;
    }

    public Animation<RMValueLong> animate(long to, float duration) {
        return animate(to, duration, Easing.linear, 0);
    }
    public Animation<RMValueLong> animate(long to, float duration, Easing easing) {
        return animate(to, duration, easing, 0);
    }

    public Animation<RMValueLong> animate(long to, float duration, Easing easing, float delay) {
        AnimationImpl anim = (AnimationImpl) animate(get(), to, duration, easing, delay);
        anim.implicitStart = true;
        return anim;
    }

    private class AnimationImpl extends Animation<RMValueLong> {
        long from, to;
        boolean implicitStart;

        AnimationImpl(long from, long to, RMValueLong target) {
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
            getTarget().set(newValue);
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
        private RMValueLong target;
        private List<Keyframe> keyframes;

        KeyframeAnimationBuilder(long initialValue, float delay, RMValueLong target) {
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

        public @NotNull Animation<RMValueLong> finish() {
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

            Animation<RMValueLong> animation = new KeyframeAnimation(target, keyframes);
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

    private static class KeyframeAnimation extends Animation<RMValueLong> {
        private List<Keyframe> keyframes;

        KeyframeAnimation(RMValueLong target, List<Keyframe> keyframes) {
            super(target);
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
                    getTarget().set(next.value);
                } else {
                    float partialProgress = next.easing.invoke((progress - prev.time) / (next.time - prev.time));
                    getTarget().set((long)(prev.value + (next.value-prev.value) * partialProgress));
                }
            } else if (next != null) {
                getTarget().set(next.value);
            } else if (prev != null) {
                getTarget().set(prev.value);
            }

        }
    }
}

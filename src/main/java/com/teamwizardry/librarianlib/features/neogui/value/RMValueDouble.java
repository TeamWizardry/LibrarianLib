package com.teamwizardry.librarianlib.features.neogui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.NullAnimatable;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class RMValueDouble {
    private double value;
    @Nullable
    private final ChangeListener.Double change;

    public RMValueDouble(double initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueDouble(double initialValue, @NotNull ChangeListener.Double change) {
        this.value = initialValue;
        this.change = change;
    }

    /**
     * Gets the current value
     */
    public double get() {
        return value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
     */
    public void set(double value) {
        GuiAnimator.getCurrent().add(animatable);
        this.value = value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public double getValue(Object thisRef, KProperty property) {
        return value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty property, double value) {
        set(value);
    }

    private Animatable animatable = new Animatable();
    private class Animatable implements GuiAnimatable<Animatable> {
        @Nullable
        @Override
        public Object getAnimatableValue() {
            return RMValueDouble.this.get();
        }

        @Override
        public void setAnimatableValue(@Nullable Object value) {
            RMValueDouble.this.set((double) value);
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

    public Animation<RMValueDouble> animate(double from, double to, float duration, Easing easing) {
        return animate(from, to, duration, easing, 0);
    }
    public Animation<RMValueDouble> animate(double from, double to, float duration) {
        return animate(from, to, duration, Easing.linear, 0);
    }

    public Animation<RMValueDouble> animate(double from, double to, float duration, Easing easing, float delay) {
        AnimationImpl animation = new AnimationImpl(from, to, this);
        animation.setDuration(duration);
        animation.easing = easing;
        animation.setStart(delay);
        Animator.global.add(animation);
        return animation;
    }

    public Animation<RMValueDouble> animate(double to, float duration) {
        return animate(to, duration, Easing.linear, 0);
    }
    public Animation<RMValueDouble> animate(double to, float duration, Easing easing) {
        return animate(to, duration, easing, 0);
    }

    public Animation<RMValueDouble> animate(double to, float duration, Easing easing, float delay) {
        AnimationImpl anim = (AnimationImpl) animate(get(), to, duration, easing, delay);
        anim.implicitStart = true;
        return anim;
    }

    private class AnimationImpl extends Animation<RMValueDouble> {
        double from, to;
        boolean implicitStart;

        AnimationImpl(double from, double to, RMValueDouble target) {
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
            getTarget().set(newValue);
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
        private RMValueDouble target;
        private List<Keyframe> keyframes;

        KeyframeAnimationBuilder(double initialValue, float delay, RMValueDouble target) {
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

        public @NotNull Animation<RMValueDouble> finish() {
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

            Animation<RMValueDouble> animation = new KeyframeAnimation(target, keyframes);
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

    private static class KeyframeAnimation extends Animation<RMValueDouble> {
        private List<Keyframe> keyframes;

        KeyframeAnimation(RMValueDouble target, List<Keyframe> keyframes) {
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
                    getTarget().set(next.value);
                } else {
                    float partialProgress = next.easing.invoke((progress - prev.time) / (next.time - prev.time));
                    getTarget().set(prev.value + (next.value-prev.value) * partialProgress);
                }
            } else if (next != null) {
                getTarget().set(next.value);
            } else if (prev != null) {
                getTarget().set(prev.value);
            }

        }
    }
}

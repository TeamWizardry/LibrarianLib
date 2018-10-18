package com.teamwizardry.librarianlib.features.gui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMValueLong {
    private long value;

    public RMValueLong(long initialValue) {
        this.value = initialValue;
    }

    /**
     * Gets the current value
     */
    public long get() {
        return value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
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
}

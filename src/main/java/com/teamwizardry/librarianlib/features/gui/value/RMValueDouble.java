package com.teamwizardry.librarianlib.features.gui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private class Animatable implements GuiAnimatable {
        @Nullable
        @Override
        public Object getAnimatableValue() {
            return get();
        }

        @Override
        public void setAnimatableValue(@Nullable Object value) {
            set((double) value);
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
            getTarget().set(newValue);
        }
    }
}

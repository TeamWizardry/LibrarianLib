package com.teamwizardry.librarianlib.features.gui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMValueInt {
    private int value;
    @Nullable
    private final ChangeListener.Int change;

    public RMValueInt(int initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueInt(int initialValue, @NotNull ChangeListener.Int change) {
        this.value = initialValue;
        this.change = change;
    }

    /**
     * Gets the current value
     */
    public int get() {
        return value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
     */
    public void set(int value) {
        GuiAnimator.getCurrent().add(animatable);
        this.value = value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public int getValue(Object thisRef, KProperty property) {
        return value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty property, int value) {
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
            set((int) value);
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

    public void animate(int from, int to, float duration, Easing easing) { animate(from, to, duration, easing, 0); }
    public void animate(int from, int to, float duration) { animate(from, to, duration, Easing.linear, 0); }

    public void animate(int from, int to, float duration, Easing easing, float delay) {
        AnimationImpl animation = new AnimationImpl(from, to, this);
        animation.setDuration(duration);
        animation.easing = easing;
        animation.setStart(delay);
        Animator.global.add(animation);
    }

    public void animate(int to, float duration) { animate(to, duration, Easing.linear, 0); }
    public void animate(int to, float duration, Easing easing) { animate(to, duration, easing, 0); }

    public void animate(int to, float duration, Easing easing, float delay) {
        AnimationImpl animation = new AnimationImpl(get(), to, this);
        animation.implicitStart = true;
        animation.setDuration(duration);
        animation.easing = easing;
        animation.setStart(delay);
        Animator.global.add(animation);
    }

    private class AnimationImpl extends Animation<RMValueInt> {
        int from, to;
        boolean implicitStart;

        AnimationImpl(int from, int to, RMValueInt target) {
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
            int newValue = (int)(from + (to-from) * progress);
            getTarget().set(newValue);
        }
    }
}

package com.teamwizardry.librarianlib.features.gui.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class RMValueBoolean {
    private boolean value;

    public RMValueBoolean(boolean initialValue) {
        this.value = initialValue;
    }

    /**
     * Gets the current value
     */
    public boolean get() {
        return value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
     */
    public void set(boolean value) {
        GuiAnimator.getCurrent().add(animatable);
        this.value = value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public boolean getValue(Object thisRef, KProperty property) {
        return value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty property, boolean value) {
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
            set((boolean) value);
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

    public void animate(boolean from, boolean to, float delay) {
        AnimationImpl animation = new AnimationImpl(from, to, this);
        animation.setDuration(delay);
        Animator.global.add(animation);
    }

    private class AnimationImpl extends Animation<RMValueBoolean> {
        boolean from, to;
        boolean implicitStart;

        AnimationImpl(boolean from, boolean to, RMValueBoolean target) {
            super(target);
            this.from = from;
            this.to = to;
        }

        public void update(float time) {
            if(implicitStart) {
                from = getTarget().get();
                implicitStart = false;
            }
            getTarget().set(time == 1 ? to : from);
        }
    }
}

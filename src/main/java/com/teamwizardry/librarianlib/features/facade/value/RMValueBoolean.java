package com.teamwizardry.librarianlib.features.facade.value;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.NullAnimatable;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("Duplicates")
public class RMValueBoolean {
    private boolean value;
    @Nullable
    private final ChangeListener.Boolean change;

    public RMValueBoolean(boolean initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueBoolean(boolean initialValue, @NotNull ChangeListener.Boolean change) {
        this.value = initialValue;
        this.change = change;
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
    private class Animatable implements GuiAnimatable<Animatable> {
        @Nullable
        @Override
        public Object getAnimatableValue() {
            return RMValueBoolean.this.get();
        }

        @Override
        public void setAnimatableValue(@Nullable Object value) {
            RMValueBoolean.this.set((boolean) value);
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

    public Animation<RMValueBoolean> animate(boolean to, float delay) {
        AnimationImpl anim = (AnimationImpl) animate(get(), to, delay);
        anim.implicitStart = true;
        return anim;
    }
    public Animation<RMValueBoolean> animate(boolean from, boolean to, float delay) {
        AnimationImpl animation = new AnimationImpl(from, to, this);
        animation.setDuration(delay);
        Animator.global.add(animation);
        return animation;
    }

    private class AnimationImpl extends Animation<RMValueBoolean> {
        boolean from, to;
        boolean implicitStart;

        AnimationImpl(boolean from, boolean to, RMValueBoolean target) {
            super(target, new NullAnimatable<>());
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

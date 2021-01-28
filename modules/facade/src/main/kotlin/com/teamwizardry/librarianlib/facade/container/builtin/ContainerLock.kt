package com.teamwizardry.librarianlib.facade.container.builtin

import java.util.function.Supplier

public fun interface ContainerLock {
    public fun isLocked(): Boolean

    public class ManualLock(private var isLocked: Boolean) : ContainerLock {
        public fun setLocked(value: Boolean) {
            this.isLocked = value
        }

        public fun lock() {
            this.isLocked = true
        }

        public fun unlock() {
            this.isLocked = false
        }

        override fun isLocked(): Boolean {
            return isLocked
        }
    }

    public class StaticLock(private val isLocked: Boolean) : ContainerLock {
        override fun isLocked(): Boolean {
            return isLocked
        }
    }

    /**
     * A slot intended to prevent certain kinds of item duplication glitches. To do this, it records the value returned
     * from the [verificationCallback] and before every operation it verifies that the callback returns the same object.
     *
     * The specific dupe this prevents is (using a backpack as an example):
     *
     * - the player opens a backpack item
     * - you open a container and add the held backpack's inventory to it
     * - the player puts items in the backpack inventory
     * - the player uses a "player interface" of some sort to take the backpack out of their inventory and put it in a
     * chest, which copies the itemstack and discards the existing instance
     * - your screen is now accessing an "orphaned" backpack, so the player can pull items out without affecting the
     * instance in the chest
     * - congratulations, you've just introduced a new dupe!
     *
     * ***!NOTE!***
     * It seems the default inventory code in 1.16+ does *not* actually *replace* the ItemStack in the inventory, it
     * just *modifies* it. This means you can *not* use the actual stack, you'll have to use something like a capability
     * instead. (e.g. `player.getItemInHand(...).getCapability(...)`)
     *
     * The code is designed to be more general though, so you can use any object as the verification.
     *
     *
     * @param verificationCallback A callback that, if its value changes, will lock the slot.
     * @param isClient Whether this slot is in the client container. ItemStack object identity in particular is not
     * preserved on the client, so the check has to be disabled there.
     */
    public class ConsistencyLock(
        private val isClient: Boolean,
        private val verificationCallback: Supplier<Any>
    ) : ContainerLock {
        private val verificationReference: Any = verificationCallback.get()

        override fun isLocked(): Boolean {
            if (isClient)
                return false
            return verificationCallback.get() !== verificationReference
        }
    }

    public companion object {
        @JvmField
        public val LOCKED: ContainerLock = StaticLock(true)
        @JvmField
        public val UNLOCKED: ContainerLock = StaticLock(false)
    }
}
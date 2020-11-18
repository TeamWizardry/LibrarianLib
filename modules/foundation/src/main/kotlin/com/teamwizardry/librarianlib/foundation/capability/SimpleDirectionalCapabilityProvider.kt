package com.teamwizardry.librarianlib.foundation.capability

import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.common.util.LazyOptional

/**
 * A simple capability provider that serializes and returns an instance for a single side. For one that is
 * direction-independent, see [SimpleCapabilityProvider].
 *
 * For more than one direction don't use multiple instances of this class, make your own anonymous subclass.
 * Capabilities need to have high performance, so having extra providers simply to have multiple sides is decidedly
 * *not* performant.
 *
 * Here's an example you can copy/paste:
 * ```java
 * new ICapabilitySerializable<CompoundNBT>() {
 *     private final TopType topInstance = new TopType();
 *     private final BottomType bottomInstance = new BottomType();
 *
 *     private final LazyOptional<TopType> topOptInstance = LazyOptional.of(() -> topInstance);
 *     private final LazyOptional<BottomType> bottomOptInstance = LazyOptional.of(() -> bottomInstance);
 *
 *     @Override
 *     public CompoundNBT serializeNBT() {
 *         CompoundNBT nbt = new CompoundNBT();
 *         nbt.put("top", topInstance.serializeNBT());
 *         nbt.put("bottom", bottomInstance.serializeNBT());
 *         return nbt;
 *     }
 *
 *     @Override
 *     public void deserializeNBT(CompoundNBT nbt) {
 *         topInstance.deserializeNBT(nbt.getCompound("top"));
 *         bottomInstance.deserializeNBT(nbt.getCompound("bottom"));
 *     }
 *
 *     @NotNull
 *     @Override
 *     public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
 *         if(cap == topTypeCapability && side == Direction.UP) {
 *             return topOptInstance.cast();
 *         }
 *         if(cap == bottomTypeCapability && side == Direction.DOWN) {
 *             return bottomOptInstance.cast();
 *         }
 *         return LazyOptional.empty();
 *     }
 * };
 * ```
 */
public class SimpleDirectionalCapabilityProvider<T: INBTSerializable<N>, N: INBT>(
    private val capability: Capability<T>,
    private val instance: T,
    private val direction: Direction?
): ICapabilitySerializable<N> {
    private val optInstance = LazyOptional.of { instance }

    override fun serializeNBT(): N {
        return instance.serializeNBT()
    }

    override fun deserializeNBT(nbt: N) {
        instance.deserializeNBT(nbt)
    }

    override fun <T: Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if(cap == capability && side == direction)
            return optInstance.cast()
        return LazyOptional.empty()
    }
}
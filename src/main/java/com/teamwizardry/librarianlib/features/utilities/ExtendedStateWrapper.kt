package com.teamwizardry.librarianlib.features.utilities

import com.google.common.collect.ImmutableMap
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateBase
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty

open class ExtendedStateWrapper(private val wrapped: IBlockState, val world: IBlockAccess, val pos: BlockPos) : BlockStateBase(), IBlockState by wrapped, IExtendedBlockState {

    constructor(state: IBlockState, parent: ExtendedStateWrapper) : this(state, parent.world, parent.pos)

    private val extState = wrapped as? IExtendedBlockState
    private val clean = extState?.clean ?: wrapped

    override fun getUnlistedNames(): Collection<IUnlistedProperty<*>>? {
        return extState?.unlistedNames ?: emptyList()
    }

    override fun <V> getValue(property: IUnlistedProperty<V>?)
            = extState?.getValue(property)

    override fun <V> withProperty(property: IUnlistedProperty<V>?, value: V?)
            = extState?.let { ExtendedStateWrapper(it.withProperty(property, value), this) } ?: this

    override fun getUnlistedProperties(): ImmutableMap<IUnlistedProperty<*>, java.util.Optional<*>>
            = extState?.unlistedProperties ?: ImmutableMap.of<IUnlistedProperty<*>, java.util.Optional<*>>()

    override fun getClean() = clean

    override fun <T : Comparable<T>> getValue(property: IProperty<T>): T = wrapped.getValue(property)

    override fun <T : Comparable<T>, V : T> withProperty(property: IProperty<T>, value: V)
            = ExtendedStateWrapper(wrapped.withProperty(property, value), this)

    override fun <T : Comparable<T>> cycleProperty(property: IProperty<T>)
            = ExtendedStateWrapper(wrapped.cycleProperty(property), this)

}

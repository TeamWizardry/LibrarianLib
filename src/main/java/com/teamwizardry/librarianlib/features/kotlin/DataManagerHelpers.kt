@file:Suppress("NOTHING_TO_INLINE") // Inlining for datamanager specifics

package com.teamwizardry.librarianlib.features.kotlin

import com.google.common.base.Optional
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializer
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Rotations
import net.minecraft.util.text.ITextComponent
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @author WireSegal
 * Created at 3:43 PM on 7/12/18.
 */

// Creation methods


inline fun KClass<out Entity>.createByteKey(): DataParameter<Byte> = createKey(DataSerializers.BYTE)
inline fun KClass<out Entity>.createIntKey(): DataParameter<Int> = createKey(DataSerializers.VARINT)
inline fun KClass<out Entity>.createFloatKey(): DataParameter<Float> = createKey(DataSerializers.FLOAT)
inline fun KClass<out Entity>.createStringKey(): DataParameter<String> = createKey(DataSerializers.STRING)
inline fun KClass<out Entity>.createTextComponentKey(): DataParameter<ITextComponent> = createKey(DataSerializers.TEXT_COMPONENT)
inline fun KClass<out Entity>.createStackKey(): DataParameter<ItemStack> = createKey(DataSerializers.ITEM_STACK)
inline fun KClass<out Entity>.createBlockStateKey(): DataParameter<Optional<IBlockState>> = createKey(DataSerializers.OPTIONAL_BLOCK_STATE)
inline fun KClass<out Entity>.createBooleanKey(): DataParameter<Boolean> = createKey(DataSerializers.BOOLEAN)
inline fun KClass<out Entity>.createRotationsKey(): DataParameter<Rotations> = createKey(DataSerializers.ROTATIONS)
inline fun KClass<out Entity>.createPosKey(): DataParameter<BlockPos> = createKey(DataSerializers.BLOCK_POS)
inline fun KClass<out Entity>.createOptionalPosKey(): DataParameter<Optional<BlockPos>> = createKey(DataSerializers.OPTIONAL_BLOCK_POS)
inline fun KClass<out Entity>.createFacingKey(): DataParameter<EnumFacing> = createKey(DataSerializers.FACING)
inline fun KClass<out Entity>.createUUIDKey(): DataParameter<Optional<UUID>> = createKey(DataSerializers.OPTIONAL_UNIQUE_ID)
inline fun KClass<out Entity>.createCompoundKey(): DataParameter<NBTTagCompound> = createKey(DataSerializers.COMPOUND_TAG)

inline fun <T> KClass<out Entity>.createKey(noinline writer: (PacketBuffer, T) -> Unit,
                                            noinline reader: (PacketBuffer) -> T): DataParameter<T> =
        createKey(writer, reader) { it }

inline fun <T> KClass<out Entity>.createKey(noinline writer: (PacketBuffer, T) -> Unit,
                                            noinline reader: (PacketBuffer) -> T,
                                            noinline copy: (T) -> T): DataParameter<T> =
        createKey(FunctionalDataSerializer(writer, reader, copy).also(DataSerializers::registerSerializer))

inline fun <T> KClass<out Entity>.createKey(serializer: DataSerializer<T>): DataParameter<T> =
        EntityDataManager.createKey(this.java, serializer)

fun <T> Entity.with(serializer: DataParameter<T>, default: T) = dataManager.with(serializer, default)

fun <T> EntityDataManager.with(serializer: DataParameter<T>, default: T) = apply { register(serializer, default) }

class FunctionalDataSerializer<T>(val writer: (PacketBuffer, T) -> Unit,
                                          val reader: (PacketBuffer) -> T,
                                          val copy: (T) -> T) : DataSerializer<T> {
    override fun createKey(id: Int): DataParameter<T> = DataParameter(id, this)
    override fun copyValue(value: T): T = copy(value)
    override fun write(buf: PacketBuffer, value: T) = writer(buf, value)
    override fun read(buf: PacketBuffer): T = reader(buf)
}

// Properties

class DataManagerProperty<E : Entity, T : Any>(val dataParameter: DataParameter<T>) : ReadWriteProperty<E, T> {
    override fun getValue(thisRef: E, property: KProperty<*>): T = thisRef.dataManager.get(dataParameter)
    override fun setValue(thisRef: E, property: KProperty<*>, value: T) = thisRef.dataManager.set(dataParameter, value)
}

class OptionalDataManagerProperty<E : Entity, T : Any>(val dataParameter: DataParameter<Optional<T>>) : ReadWriteProperty<E, T?> {
    override fun getValue(thisRef: E, property: KProperty<*>): T? = thisRef.dataManager.get(dataParameter).orNull()
    override fun setValue(thisRef: E, property: KProperty<*>, value: T?) = thisRef.dataManager.set(dataParameter, Optional.fromNullable(value))
}

@JvmName("managedOptionalValue")
fun <E : Entity, T : Any> managedValue(dataParameter: DataParameter<Optional<T>>) = OptionalDataManagerProperty<E, T>(dataParameter)

fun <E : Entity, T : Any> managedValue(dataParameter: DataParameter<T>) = DataManagerProperty<E, T>(dataParameter)



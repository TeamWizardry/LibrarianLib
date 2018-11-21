package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.helpers.castOrDefault
import com.teamwizardry.librarianlib.features.kotlin.forEachIndexed
import com.teamwizardry.librarianlib.features.kotlin.readBooleanArray
import com.teamwizardry.librarianlib.features.kotlin.writeBooleanArray
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.fml.relauncher.ReflectionHelper

/**
 * Created by TheCodeWarrior
 */
@SerializerFactoryRegister
object SerializeListFactory : SerializerFactory("List") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return this.canApplySubclass(type, List::class.java)
    }

    override fun create(type: FieldType): Serializer<*> {
        return SerializeList(type, type.resolveGeneric(List::class.java, 0))
    }

    class SerializeList(type: FieldType, generic: FieldType) : Serializer<MutableList<Any?>>(type) {

        override fun getDefault(): MutableList<Any?> {
            return constructor()
        }

        val serGeneric: Serializer<Any> by SerializerRegistry.lazy(generic)
        val constructor = createConstructorMH()

        override fun readNBT(nbt: NBTBase, existing: MutableList<Any?>?, syncing: Boolean): MutableList<Any?> {
            val list = nbt.castOrDefault(NBTTagList::class.java)

            @Suppress("UNCHECKED_CAST")
            val array = (existing ?: getDefault())

            while (array.size > list.tagCount())
                array.removeAt(array.size - 1)

            list.forEachIndexed<NBTTagCompound> { i, container ->
                val tag = container.getTag("-")
                val v = serGeneric.read(tag, array.getOrNull(i), syncing)
                if (i >= array.size) {
                    array.add(v)
                } else {
                    array[i] = v
                }
            }

            return array
        }

        override fun writeNBT(value: MutableList<Any?>, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            for (i in 0 until value.size) {
                val container = NBTTagCompound()
                list.appendTag(container)
                val v = value[i]
                if (v != null) {
                    container.setTag("-", serGeneric.write(v, syncing))
                }
            }

            return list
        }

        override fun readBytes(buf: ByteBuf, existing: MutableList<Any?>?, syncing: Boolean): MutableList<Any?> {
            val nullsig = buf.readBooleanArray()

            @Suppress("UNCHECKED_CAST")
            val array = (existing ?: getDefault())

            while (array.size > nullsig.size)
                array.removeAt(array.size - 1)

            for (i in 0 until nullsig.size) {
                val v = if (nullsig[i]) null else serGeneric.read(buf, array.getOrNull(i), syncing)
                if (i >= array.size) {
                    array.add(v)
                } else {
                    array[i] = v
                }
            }
            return array
        }

        override fun writeBytes(buf: ByteBuf, value: MutableList<Any?>, syncing: Boolean) {
            val nullsig = BooleanArray(value.size) { value[it] == null }
            buf.writeBooleanArray(nullsig)

            (0 until value.size)
                    .filterNot { nullsig[it] }
                    .forEach { serGeneric.write(buf, value[it]!!, syncing) }
        }

        private fun createConstructorMH(): () -> MutableList<Any?> {
            if (type.clazz == List::class.java) {
                return { mutableListOf() }
            } else {
                try {
                    val mh = MethodHandleHelper.wrapperForConstructor<MutableList<Any?>>(type.clazz)
                    return { mh(arrayOf()) }
                } catch(e: ReflectionHelper.UnableToFindMethodException) {
                    return { throw UnsupportedOperationException("Could not find zero-argument constructor for " +
                            type.clazz.simpleName, e) }
                }
            }
        }
    }
}

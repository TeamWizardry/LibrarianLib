package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.common.util.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.common.util.forEachIndexed
import com.teamwizardry.librarianlib.common.util.handles.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.readVarInt
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.FieldType
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.writeVarInt
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*

/**
 * Created by TheCodeWarrior
 */
@SerializerFactoryRegister
object SerializeSetFactory : SerializerFactory("Set") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return this.canApplySubclass(type, Set::class.java)
    }

    override fun create(type: FieldType): Serializer<*> {
        val superclass = type.genericSuperclass(Set::class.java) as FieldTypeGeneric
        return SerializeSet(type, superclass.generic(0))
    }

    class SerializeSet(type: FieldType, valueType: FieldType) : Serializer<MutableSet<Any?>>(type) {

        val serValue: Serializer<Any> by SerializerRegistry.lazy(valueType)

        val constructor = createConstructorMethodHandle()

        override fun readNBT(nbt: NBTBase, existing: MutableSet<Any?>?, syncing: Boolean): MutableSet<Any?> {
            val compound = nbt.safeCast(NBTTagCompound::class.java)
            val list = compound.getTag("values").safeCast(NBTTagList::class.java)
            val nullFlag = compound.getBoolean("hasNull")

            @Suppress("UNCHECKED_CAST")
            val set = existing ?: constructor()
            set.clear()
            if (nullFlag)
                set.add(null)
            list.forEachIndexed<NBTTagCompound> { i, tag ->
                val v = serValue.read(tag, null, syncing)
                set.add(v)
            }

            return set
        }

        override fun writeNBT(value: MutableSet<Any?>, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            value
                    .filterNotNull()
                    .forEach { list.appendTag(serValue.write(it, syncing)) }

            val compound = NBTTagCompound()
            compound.setBoolean("hasNull", value.contains(null))
            compound.setTag("values", list)
            return compound
        }

        override fun readBytes(buf: ByteBuf, existing: MutableSet<Any?>?, syncing: Boolean): MutableSet<Any?> {
            val nullFlag = buf.readBoolean()
            val len = buf.readVarInt() - if (nullFlag) 1 else 0

            @Suppress("UNCHECKED_CAST")
            val set = existing ?: constructor()
            set.clear()
            if (nullFlag)
                set.add(null)

            for (i in 0..len - 1) {
                set.add(serValue.read(buf, null, syncing))
            }

            return set
        }

        override fun writeBytes(buf: ByteBuf, value: MutableSet<Any?>, syncing: Boolean) {
            buf.writeBoolean(value.contains(null))
            buf.writeVarInt(value.size)

            value
                    .filterNotNull()
                    .forEach { serValue.write(buf, it, syncing) }
        }

        private fun createConstructorMethodHandle(): () -> MutableSet<Any?> {

                    if(type.clazz == Set::class.java) {
                        return { LinkedHashSet<Any?>() } // linked so if order is important it's preserved.
                    } else if (EnumSet::class.java.isAssignableFrom(type.clazz)) {
                        @Suppress("UNCHECKED_CAST")
                        return { RawEnumSetCreator.create(type.clazz) as MutableSet<Any?> }
                    } else {
                        val mh = MethodHandleHelper.wrapperForConstructor<MutableSet<Any?>>(type.clazz)
                        return { mh(arrayOf()) }
                    }
        }
    }
}

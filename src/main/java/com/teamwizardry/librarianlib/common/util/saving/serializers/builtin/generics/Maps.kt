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
object SerializeMapFactory : SerializerFactory("Map") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return this.canApplySubclass(type, Map::class.java)
    }

    override fun create(type: FieldType): Serializer<*> {
        val superclass = type.genericSuperclass(Map::class.java) as FieldTypeGeneric
        return SerializeMap(type, superclass.generic(0), superclass.generic(1))
    }

    class SerializeMap(type: FieldType, keyType: FieldType, valueType: FieldType) : Serializer<MutableMap<Any?, Any?>>(type) {

        val serKey: Serializer<Any> by SerializerRegistry.lazy(keyType)
        val serValue: Serializer<Any> by SerializerRegistry.lazy(valueType)

        val constructor = createConstructorMethodHandle()

        override fun readNBT(nbt: NBTBase, existing: MutableMap<Any?, Any?>?, syncing: Boolean): MutableMap<Any?, Any?> {
            val list = nbt.safeCast<NBTTagList>()
            val map = constructor()

            list.forEachIndexed<NBTTagCompound> { i, container ->
                val keyTag = container.getTag("k")
                val valTag = container.getTag("v")
                val k = if (keyTag == null) null else serKey.read(keyTag, null, syncing)
                val v = if (valTag == null) null else serValue.read(valTag, existing?.get(k), syncing)
                map[k] = v
            }

            return map
        }

        override fun writeNBT(value: MutableMap<Any?, Any?>, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            for (k in value.keys) {
                val container = NBTTagCompound()
                list.appendTag(container)
                val v = value[k]

                if (k != null) {
                    container.setTag("k", serKey.write(k, syncing))
                }
                if (v != null) {
                    container.setTag("v", serValue.write(v, syncing))
                }
            }

            return list
        }

        override fun readBytes(buf: ByteBuf, existing: MutableMap<Any?, Any?>?, syncing: Boolean): MutableMap<Any?, Any?> {
            val map = constructor()

            val nullCount = buf.readVarInt()
            for (i in 0..nullCount - 1) {
                val k = serKey.read(buf, null, syncing)
                map[k] = null
            }

            val hasNullKey = buf.readBoolean()
            if (hasNullKey) {
                val isNullValue = buf.readBoolean()
                map[null] = if (isNullValue) null else serValue.read(buf, existing?.get(null), syncing)
            }

            val nonNullCount = buf.readVarInt()
            for (i in 0..nonNullCount - 1) {
                val k = serKey.read(buf, null, syncing)
                val v = serValue.read(buf, existing?.get(k), syncing)
                map[k] = v
            }

            return map
        }

        override fun writeBytes(buf: ByteBuf, value: MutableMap<Any?, Any?>, syncing: Boolean) {
            val nulls = value.filter { it.value == null && it.key != null }
            buf.writeVarInt(nulls.count { it.key != null })
            nulls.forEach { serKey.write(buf, it.key!!, syncing) }

            buf.writeBoolean(value.containsKey(null))
            if (value.containsKey(null)) {
                buf.writeBoolean(value[null] == null)
                if (value[null] != null)
                    serValue.write(buf, value[null]!!, syncing)
            }

            val nonNulls = value.filter { it.value != null && it.key != null }
            buf.writeVarInt(nonNulls.size)
            nonNulls.forEach {
                serKey.write(buf, it.key!!, syncing)
                serValue.write(buf, it.value!!, syncing)
            }
        }

        private fun createConstructorMethodHandle(): () -> MutableMap<Any?, Any?> {
            if(type.clazz == Map::class.java) {
                return { LinkedHashMap<Any?, Any?>() }
            } else {
                val mh = MethodHandleHelper.wrapperForConstructor<MutableMap<Any?, Any?>>(type.clazz)
                return { mh(arrayOf()) }
            }
        }
    }
}

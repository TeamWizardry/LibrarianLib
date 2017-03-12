package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special

import com.google.gson.internal.`$Gson$Types`
import com.teamwizardry.librarianlib.common.util.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.common.util.handles.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.readBooleanArray
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.*
import com.teamwizardry.librarianlib.common.util.saving.serializers.*
import com.teamwizardry.librarianlib.common.util.writeBooleanArray
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import java.lang.reflect.Constructor
import java.util.*

/**
 * Created by TheCodeWarrior
 */
@SerializerFactoryRegister
object SerializeObjectFactory : SerializerFactory("Object") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        val savable = type.clazz.isAnnotationPresent(Savable::class.java)
        val inplace = inPlaceCheck(type.clazz)
        if(savable || inplace)
            return SerializerFactoryMatch.GENERAL
        else
            return SerializerFactoryMatch.NONE
    }

    fun inPlaceCheck(clazz: Class<*>): Boolean {
        if (clazz.isAnnotationPresent(SaveInPlace::class.java))
            return true
        return clazz.superclass?.let { inPlaceCheck(it) } ?: false
    }

    override fun create(type: FieldType): Serializer<*> {
        return SerializeObject(type, SerializerAnalysis(type))
    }

    class SerializeObject(type: FieldType, val analysis: SerializerAnalysis) : Serializer<Any>(type) {

        override fun readNBT(nbt: NBTBase, existing: Any?, sync: Boolean): Any {
            val tag = nbt.safeCast(NBTTagCompound::class.java)

            if (analysis.mutable) {
                val instance = existing ?: analysis.constructorMH(arrayOf())
                readFields(analysis.alwaysFields, tag, instance, sync)

                if (!sync) {
                    readFields(analysis.noSyncFields, tag, instance, sync)
                } else {
                    readFields(analysis.nonPersistentFields, tag, instance, sync)
                }
                return instance
            } else {
                return analysis.constructorMH(analysis.constructorArgOrder.map {
                    if (tag.hasKey(it))
                        analysis.serializers[it]!!.value.read(tag.getTag(it), null, sync)
                    else
                        null
                }.toTypedArray())
            }
        }

        fun readFields(map: Map<String, FieldCache>, tag: NBTTagCompound, instance: Any, sync: Boolean) {
            map.forEach {
                val oldValue = it.value.getter(instance)
                val value = if (tag.hasKey(it.key)) {
                    analysis.serializers[it.key]!!.value.read(tag.getTag(it.key), oldValue, sync)
                } else {
                    if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) oldValue else null
                }
                if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) {
                    if(oldValue !== value) {
                        throw SerializerException("Cannot set final field ${it.value.name} in class $type to new value. Either make the field mutable or modify the serializer to change the existing object instead of creating a new one.")
                    }
                } else {
                    it.value.setter(instance, value)
                }
            }
        }

        override fun writeNBT(value: Any, syncing: Boolean): NBTBase {
            val tag = NBTTagCompound()

            writeFields(analysis.alwaysFields, value, tag, syncing)

            if (!syncing) {
                writeFields(analysis.noSyncFields, value, tag, syncing)
            } else {
                writeFields(analysis.nonPersistentFields, value, tag, syncing)
            }

            return tag
        }

        fun writeFields(map: Map<String, FieldCache>, value: Any, tag: NBTTagCompound, sync: Boolean) {
            map.forEach {
                val fieldValue = it.value.getter(value)
                if (fieldValue != null)
                    tag.setTag(it.key, analysis.serializers[it.key]!!.value.write(fieldValue, sync))
            }
        }

        override fun readBytes(buf: ByteBuf, existing: Any?, syncing: Boolean): Any {
            val nullsig = buf.readBooleanArray()
            val nulliter = nullsig.iterator()
            if (analysis.mutable) {
                val instance = existing ?: analysis.constructorMH(arrayOf())
                readFields(analysis.alwaysFields, buf, instance, nulliter, syncing)
                if (!syncing) {
                    readFields(analysis.noSyncFields, buf, instance, nulliter, syncing)
                } else {
                    readFields(analysis.nonPersistentFields, buf, instance, nulliter, syncing)
                }
                return instance
            } else {
                val map = mutableMapOf<String, Any?>()

                analysis.alwaysFields.forEach {
                    if (!nulliter.next()) {
                        map[it.key] = analysis.serializers[it.key]!!.value.read(buf, null, syncing)
                    }
                }
                if (!syncing) {
                    analysis.noSyncFields.forEach {
                        if (!nulliter.next()) {
                            map[it.key] = analysis.serializers[it.key]!!.value.read(buf, null, syncing)
                        }
                    }
                } else {
                    analysis.nonPersistentFields.forEach {
                        if (!nulliter.next()) {
                            map[it.key] = analysis.serializers[it.key]!!.value.read(buf, null, syncing)
                        }
                    }
                }
                return analysis.constructorMH(analysis.constructorArgOrder.map {
                    map[it]
                }.toTypedArray())
            }
        }

        private fun readFields(map: Map<String, FieldCache>, buf: ByteBuf, instance: Any, nullsig: BooleanIterator, sync: Boolean) {
            analysis.nonPersistentFields.forEach {
                val oldValue = it.value.getter(instance)
                val value = if (nullsig.next()) {
                    null
                } else {
                    analysis.serializers[it.key]!!.value.read(buf, oldValue, sync)
                }
                if(it.value.meta.hasFlag(SavingFieldFlag.FINAL)) {
                    if(oldValue !== value) {
                        throw SerializerException("Cannot set final field ${it.value.name} in class ${type} to new value. Either make the field mutable or modify the serializer to change the existing object instead of creating a new one.")
                    }
                } else {
                    it.value.setter(instance, value)
                }
            }

        }

        override fun writeBytes(buf: ByteBuf, value: Any, syncing: Boolean) {
            val nullsig = if (syncing) {
                analysis.alwaysFields.map { it.value.getter(value) == null }.toTypedArray().toBooleanArray()
            } else {
                analysis.allFieldsOrdered.map { it.value.getter(value) == null }.toTypedArray().toBooleanArray()
            }
            buf.writeBooleanArray(nullsig)

            analysis.alwaysFields.forEach {
                val fieldValue = it.value.getter(value)
                if (fieldValue != null)
                    analysis.serializers[it.key]!!.value.write(buf, fieldValue, syncing)
            }

            if (!syncing) {
                analysis.noSyncFields.forEach {
                    val fieldValue = it.value.getter(value)
                    if (fieldValue != null)
                        analysis.serializers[it.key]!!.value.write(buf, fieldValue, syncing)
                }
            } else {
                analysis.nonPersistentFields.forEach {
                    val fieldValue = it.value.getter(value)
                    if (fieldValue != null)
                        analysis.serializers[it.key]!!.value.write(buf, fieldValue, syncing)
                }
            }
        }
    }
}


class SerializerAnalysis(val type: FieldType) {
    val alwaysFields: Map<String, FieldCache>
    val noSyncFields: Map<String, FieldCache>
    val nonPersistentFields: Map<String, FieldCache>
    val allFieldsOrdered: Map<String, FieldCache>
    val fields: Map<String, FieldCache>

    val mutable: Boolean

    val inPlaceSavable: Boolean

    val constructor: Constructor<*>
    val constructorArgOrder: List<String>
    val constructorMH: (Array<Any?>) -> Any
    val serializers: Map<String, Lazy<Serializer<Any>>>

    init {
        val allFields = mutableMapOf<String, FieldCache>()
        addFieldsRecursive(allFields, type)
        this.fields =
                if (allFields.any { it.value.meta.hasFlag(SavingFieldFlag.ANNOTATED) }) {
                    allFields.filter {
                        it.value.meta.hasFlag(SavingFieldFlag.ANNOTATED)
                    }
                } else if (type.clazz.isAnnotationPresent(Savable::class.java)) {
                    allFields.filter {
                        it.value.meta.hasFlag(SavingFieldFlag.FIELD) && !it.value.meta.hasFlag(SavingFieldFlag.TRANSIENT)
                    }
                } else {
                    mapOf<String, FieldCache>()
                }
        inPlaceSavable = SerializeObjectFactory.inPlaceCheck(type.clazz)
        this.mutable = inPlaceSavable || !fields.any { it.value.meta.hasFlag(SavingFieldFlag.FINAL) }
        if (!mutable && fields.any { it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) })
            throw SerializerException("Immutable type ${type.clazz.canonicalName} cannot have non-syncing fields")

        alwaysFields = fields.filter { !it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) && !it.value.meta.hasFlag(SavingFieldFlag.NON_PERSISTENT) }
        noSyncFields = fields.filter { it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) && !it.value.meta.hasFlag(SavingFieldFlag.NON_PERSISTENT) }
        nonPersistentFields = fields.filter { it.value.meta.hasFlag(SavingFieldFlag.NON_PERSISTENT) && !it.value.meta.hasFlag(SavingFieldFlag.NO_SYNC) }
        allFieldsOrdered = alwaysFields + noSyncFields + nonPersistentFields
        constructor =
                if (inPlaceSavable) {
                    nullConstructor
                } else {
                    type.clazz.declaredConstructors.find { it.parameterCount == 0 } ?:
                            type.clazz.declaredConstructors.find {
                                val paramsToFind = HashMap(fields)
                                val customParamNames = it.getDeclaredAnnotation(SavableConstructorOrder::class.java)?.params
                                var i = 0
                                it.parameters.all {
                                    val ret =
                                            if(customParamNames != null && i < customParamNames.size)
                                                paramsToFind.remove(customParamNames[i])?.meta?.type?.equals(FieldType.create(it.parameterizedType)) ?: false
                                            else
                                                paramsToFind.remove(it.name)?.meta?.type?.equals(FieldType.create(it.parameterizedType)) ?: false
                                    i++
                                    ret
                                }
                            } ?:
                            throw SerializerException("Couldn't find zero-argument constructor or constructor with parameters (${fields.map { it.value.meta.type.toString() + " " + it.key }.joinToString(", ")}) for immutable type ${type.clazz.canonicalName}")
                }
        constructorArgOrder = constructor.getDeclaredAnnotation(SavableConstructorOrder::class.java)?.params?.asList() ?: constructor.parameters.map { it.name }
        constructorMH = if (inPlaceSavable) {
            { arr -> throw SerializerException("Cannot create instance of class marked with @SaveInPlace") }
        } else {
            MethodHandleHelper.wrapperForConstructor(constructor)
        }

        serializers = fields.mapValues {
            val fieldType = it.value.meta.type
            SerializerRegistry.lazy(fieldType)
        }
    }

    private tailrec fun addFieldsRecursive(map: MutableMap<String, FieldCache>, type: FieldType) {
        map.putAll(SavingFieldCache.getClassFields(type))
        if (type.clazz != Any::class.java)
            addFieldsRecursive(map, FieldType.create(`$Gson$Types`.resolve(type.type, type.clazz, type.clazz.genericSuperclass)))
    }

    companion object {
        val nullConstructor: Constructor<*> = Any::class.java.constructors.first()
    }
}

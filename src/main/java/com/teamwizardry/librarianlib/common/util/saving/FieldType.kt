package com.teamwizardry.librarianlib.common.util.saving

import java.lang.reflect.*
import java.util.*

abstract class FieldType protected constructor(val clazz: Class<*>) {

    companion object {
        @JvmStatic
        fun create(field: Field) = create(field.genericType)

        @JvmStatic
        fun create(method: Method) = create(method.genericReturnType)

        @JvmStatic
        fun create(type: Type): FieldType {
            if(type is ParameterizedType)
                return createGeneric(type)
            if(type is GenericArrayType)
                return createGenericArray(type)
            if(type is Class<*>)
                if(type.isArray)
                    return createArray(type)
                else
                    return createPlain(type)
            throw IllegalArgumentException("Cannot create FieldType from $type")
        }

        private fun createPlain(type: Class<*>): FieldType {
            return FieldTypeClass(type)
        }

        private fun createArray(type: Class<*>): FieldType {
            return FieldTypeArray(create(type.componentType))
        }

        private fun createGeneric(type: ParameterizedType): FieldType {
            return FieldTypeGeneric(type.rawType as Class<*>, type.actualTypeArguments.map { create(it) }.toTypedArray())
        }

        private fun createGenericArray(type: GenericArrayType): FieldType {
            return FieldTypeArray(create(type.genericComponentType))
        }
    }
}

class FieldTypeClass(clazz: Class<*>) : FieldType(clazz) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldTypeClass) return false

        if (clazz != other.clazz) return false

        return true
    }

    override fun hashCode(): Int {
        return clazz.hashCode()
    }

    override fun toString(): String {
        return clazz.simpleName
    }
}

class FieldTypeArray(val componentType: FieldType) : FieldType(getArrayType(componentType)) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldTypeArray) return false

        if (componentType != other.componentType) return false

        return true
    }

    override fun hashCode(): Int {
        return componentType.hashCode()
    }

    override fun toString(): String {
        return componentType.toString() + "[]"
    }

    companion object {
        private fun getArrayType(componentType: FieldType): Class<*> {
            return java.lang.reflect.Array.newInstance(componentType.clazz, 0).javaClass
        }
    }
}

class FieldTypeGeneric(clazz: Class<*>, val generics: Array<FieldType>) : FieldType(clazz) {

    fun generic(i: Int): FieldType? {
        if(i < 0 || i >= generics.size)
            return null
        return generics[i]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldTypeGeneric) return false

        if (clazz != other.clazz) return false
        if (!Arrays.equals(generics, other.generics)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clazz.hashCode()
        result = 31 * result + Arrays.hashCode(generics)
        return result
    }

    override fun toString(): String {
        return clazz.simpleName + "<" + generics.map { it.toString() }.joinToString(", ") + ">"
    }
}

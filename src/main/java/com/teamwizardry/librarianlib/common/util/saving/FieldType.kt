package com.teamwizardry.librarianlib.common.util.saving

import java.lang.reflect.*
import java.util.*

abstract class FieldType protected constructor(val type: Type, open val clazz: Class<*>) {

    open val interfaces: Array<out Class<*>>
        get() = arrayOf()

    companion object {
        @JvmStatic
        fun create(field: Field) = create(field.genericType)

        @JvmStatic
        fun create(method: Method) = create(method.genericReturnType)

        @JvmStatic
        fun create(type: Type): FieldType {
            val fType: FieldType =
                    if (type is ParameterizedType)
                        createGeneric(type)
                    else if (type is GenericArrayType)
                        createGenericArray(type)
                    else if (type is TypeVariable<*>)
                        createVariable(type)
                    else if (type is Class<*>)
                        if (type.isArray)
                            createArray(type)
                        else
                            createPlain(type)
                    else
                        FieldTypeError()

            return fType
        }

        private fun createPlain(type: Class<*>): FieldType {
            return FieldTypeClass(type, type)
        }

        private fun createArray(type: Class<*>): FieldType {
            return FieldTypeArray(type, create(type.componentType))
        }

        private fun createGeneric(type: ParameterizedType): FieldType {
            return FieldTypeGeneric(type, type.rawType as Class<*>, type.actualTypeArguments.map { create(it) }.toTypedArray())
        }

        private fun createGenericArray(type: GenericArrayType): FieldType {
            return FieldTypeArray(type, create(type.genericComponentType))
        }

        private fun createVariable(type: TypeVariable<*>): FieldType {
            return FieldTypeVariable(type, type.genericDeclaration as Class<*>, type.name, type.genericDeclaration.typeParameters.indexOfFirst { it.name == type.name })
        }
    }
}

class FieldTypeError : FieldType(Any::class.java, Any::class.java)

class FieldTypeClass(type: Type, clazz: Class<*>) : FieldType(type, clazz) {

    override val interfaces: Array<out Class<*>> = if (clazz.isInterface) arrayOf(*clazz.interfaces, clazz) else clazz.interfaces

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

class FieldTypeArray(type: Type, val componentType: FieldType) : FieldType(type, getArrayType(componentType)) {


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

class FieldTypeGeneric(type: Type, clazz: Class<*>, val generics: Array<FieldType>) : FieldType(type, clazz) {

    fun generic(i: Int): FieldType? {
        if (i < 0 || i >= generics.size)
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

class FieldTypeVariable(type: Type, val parent: Class<*>, val name: String, val index: Int) : FieldType(type, Any::class.java) {
    override val clazz: Class<*>
        get() = throw UnsupportedOperationException("Cannot get class from variable field type!")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldTypeVariable) return false

        if (name != other.name) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + index
        return result
    }

    override fun toString(): String {
        return name
    }
}

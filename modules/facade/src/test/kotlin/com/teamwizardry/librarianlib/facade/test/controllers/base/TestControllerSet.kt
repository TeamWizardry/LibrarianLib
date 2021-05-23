package com.teamwizardry.librarianlib.facade.test.controllers.base

import com.teamwizardry.librarianlib.facade.container.FacadeControllerRegistry
import com.teamwizardry.librarianlib.facade.container.FacadeControllerType
import net.minecraft.util.Identifier
import java.lang.IllegalArgumentException

class TestControllerSet(val name: String, config: Entry.Group.() -> Unit) {
    val root: Entry.Group = Entry.Group(name)
    val types: List<Type<*, *>>

    init {
        root.config()
        types = root.collectTypes(mutableListOf())
        allTypes.addAll(types)
    }

    fun createData(): Map<Type<*, *>, TestControllerData> {
        return types.associateWith { it.dataClass.newInstance() }
    }

    fun getType(id: Identifier): Type<*, *> {
        return types.find { it.id == id }
            ?: throw IllegalArgumentException("No container type for id '$id'")
    }

    fun <T: TestController<*>> controllerType(type: Class<T>): FacadeControllerType<T> {
        @Suppress("UNCHECKED_CAST")
        return getType(makeId(type)).controllerType as FacadeControllerType<T>
    }

    sealed class Entry(val name: String) {
        abstract fun collectTypes(list: MutableList<Type<*, *>>): List<Type<*, *>>

        class Controller(name: String, val type: Type<*, *>): Entry(name) {
            override fun collectTypes(list: MutableList<Type<*, *>>): List<Type<*, *>> {
                list.add(type)
                return list
            }
        }
        class Group(name: String, val entries: MutableList<Entry> = mutableListOf()): Entry(name) {
            inline fun group(name: String, config: Group.() -> Unit) {
                val group = Group(name)
                group.config()
                entries.add(group)
            }

            inline fun <reified T : TestControllerData, C : TestController<T>> controller(
                name: String,
                containerClass: Class<C>
            ) {
                entries.add(Controller(name, Type(T::class.java, containerClass)))
            }

            override fun collectTypes(list: MutableList<Type<*, *>>): List<Type<*, *>> {
                entries.forEach {
                    it.collectTypes(list)
                }
                return list
            }
        }
    }

    class Type<T : TestControllerData, C : TestController<T>>(
        val dataClass: Class<T>,
        val controllerClass: Class<C>
    ) {
        val id: Identifier = makeId(controllerClass)
        val controllerType: FacadeControllerType<C> = FacadeControllerRegistry.register(id, controllerClass)
    }


    companion object {
        val allTypes: MutableList<Type<*, *>> = mutableListOf()

        fun getTypeByData(dataClass: Class<*>): Type<*, *> {
            return allTypes.find { it.dataClass == dataClass }
                ?: throw IllegalArgumentException("No container type for data type '${dataClass.canonicalName}'")
        }

        fun <T: TestController<*>> makeId(controllerClass: Class<T>): Identifier {
            return Identifier("liblib-facade-test", controllerClass.simpleName.lowercase())
        }
    }
}

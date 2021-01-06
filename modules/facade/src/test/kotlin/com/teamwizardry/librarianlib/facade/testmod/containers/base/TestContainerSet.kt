package com.teamwizardry.librarianlib.facade.testmod.containers.base

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.container.FacadeContainerType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import java.lang.IllegalArgumentException

class TestContainerSet(val name: String, config: Entry.Group.() -> Unit) {
    val root: Entry.Group = Entry.Group(name)
    val types: List<Type<*, *>>

    init {
        root.config()
        types = root.collectTypes(mutableListOf())
        allTypes.addAll(types)
    }

    fun createData(): Map<Type<*, *>, TestContainerData> {
        return types.associateWith { it.dataClass.newInstance() }
    }

    fun getType(id: ResourceLocation): Type<*, *> {
        return types.find { it.id == id }
            ?: throw IllegalArgumentException("No container type for id '$id'")
    }

    sealed class Entry(val name: String) {
        abstract fun collectTypes(list: MutableList<Type<*, *>>): List<Type<*, *>>

        class Container(name: String, val type: Type<*, *>): Entry(name) {
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

            inline fun <reified T : TestContainerData, reified C : TestContainer<T>> container(
                name: String,
                screenFactory: ContainerScreenFactory<C>
            ) {
                entries.add(Container(name, Type(T::class.java, C::class.java, screenFactory)))
            }

            override fun collectTypes(list: MutableList<Type<*, *>>): List<Type<*, *>> {
                entries.forEach {
                    it.collectTypes(list)
                }
                return list
            }
        }
    }

    class Type<T : TestContainerData, C : TestContainer<T>>(
        val dataClass: Class<T>,
        val containerClass: Class<C>,
        val screenFactory: ContainerScreenFactory<C>
    ) {
        val id: ResourceLocation = loc("librarianlib-facade-test", dataClass.simpleName.toLowerCase())
        val containerType: FacadeContainerType<C> = FacadeContainerType(containerClass)

        init {
            containerType.registryName = id
        }
    }

    fun interface ContainerScreenFactory<T : FacadeContainer> {
        @OnlyIn(Dist.CLIENT)
        fun create(container: T, inventory: PlayerInventory, title: ITextComponent): FacadeContainerScreen<T>
    }

    companion object {
        val allTypes: MutableList<Type<*, *>> = mutableListOf()

        fun getTypeByData(dataClass: Class<*>): Type<*, *> {
            return allTypes.find { it.dataClass == dataClass }
                ?: throw IllegalArgumentException("No container type for data type '${dataClass.canonicalName}'")
        }
    }
}
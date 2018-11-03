package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.kotlin.withRealDefault
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import java.lang.reflect.Method

internal object ComponentEventHookAnnotSearcher {
    val cache = mutableMapOf<Class<*>, EventCache>().withRealDefault { EventCache(it) }

    fun search(component: GuiLayer) {
        cache[component.javaClass].events.forEach {
            @Suppress("UNCHECKED_CAST")
            component.BUS.hook(it.first as Class<Event>) { event: Event ->
                it.second(component, event)
            }
        }
    }

    class EventCache(clazz: Class<*>) {
        val events: List<Pair<Class<*>, (Any, Event) -> Unit>>

        init {
            val methods = mutableListOf<Method>()

            var cls: Class<*>? = clazz
            while (cls != null) {
                methods.addAll(cls.declaredMethods)
                cls = cls.superclass
            }

            events = methods
                    .filter { it.isAnnotationPresent(Hook::class.java) }
                    .filter { it.parameterCount == 1 && Event::class.java.isAssignableFrom(it.parameterTypes[0]) }
                    .map {
                        it.isAccessible = true
                        val mh = MethodHandleHelper.wrapperForMethod<Any>(it)
                        @Suppress("UNCHECKED_CAST")
                        return@map it.parameterTypes[0] to { comp: Any, event: Event -> mh(comp, arrayOf(event)); Unit }
                    }
        }
    }
}

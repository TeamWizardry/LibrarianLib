package com.teamwizardry.librarianlib.features.eventbus

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import java.lang.reflect.Method

/**
 * Annotate methods with this, giving them a single parameter that inherits from [Event]. Passing this class to
 * [EventBus.register] will then register each method with its parameter type. This is automatically called for
 * [GuiLayer]s
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Hook

internal object EventHookAnnotationReflector {
    val cache = mutableMapOf<Class<*>, EventCache>()

    fun apply(bus: EventBus, obj: Any) {
        cache.getOrPut(obj.javaClass) { EventCache(obj.javaClass) }.events.forEach {
            @Suppress("UNCHECKED_CAST")
            bus.hook(it.first as Class<Event>) { event: Event ->
                it.second(obj, event)
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

package com.teamwizardry.librarianlib.utilities.eventbus

import com.teamwizardry.librarianlib.core.util.kotlin.IS_DEOBFUSCATED
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.MethodMirror
import dev.thecodewarrior.mirror.type.TypeMirror
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
    private var nextClear = System.currentTimeMillis()
    private val clearInterval = 10_000

    fun apply(bus: EventBus, obj: Any) {

        // clear cache periodically to support hotswapped classes
        if(IS_DEOBFUSCATED && System.currentTimeMillis() > nextClear) {
            nextClear = System.currentTimeMillis() + clearInterval
            cache.clear()
        }

        cache.getOrPut(obj.javaClass) { EventCache(obj.javaClass) }.events.forEach { (type, method) ->
            @Suppress("UNCHECKED_CAST")
            bus.hook(type.erasure as Class<Event>) { event: Event ->
                method(obj, event)
            }
        }
    }

    class EventCache(clazz: Class<*>) {
        val events: List<Pair<TypeMirror, MethodMirror>>

        init {
            val mirror = Mirror.reflectClass(clazz)
            events = generateSequence(mirror) { it.superclass }
                .flatMap { it.declaredMethods.asSequence() }
                .filter { it.annotations.any { it is Hook } }
                .filter { it.parameters.size == 1 && it.parameterTypes[0].isAssignableFrom(Mirror.reflectClass<Event>()) }
                .map { it.parameterTypes[0] to it }
                .toList()
        }
    }
}

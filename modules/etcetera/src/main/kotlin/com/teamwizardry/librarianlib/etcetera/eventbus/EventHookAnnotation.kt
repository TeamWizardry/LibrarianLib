package com.teamwizardry.librarianlib.etcetera.eventbus

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.MethodMirror

/**
 * Annotate methods with this, giving them a single parameter that inherits from [Event]. Passing this class to
 * [EventBus.register] will then register each method with its parameter type. This is automatically called for
 * Facade layers.
 *
 * @param priority the event priority
 * @param receiveCanceled whether the event should still receive canceled events
 */
@Target(AnnotationTarget.FUNCTION)
public annotation class Hook(val priority: EventBus.Priority = EventBus.Priority.DEFAULT, val receiveCanceled: Boolean = false)

internal object EventHookAnnotationReflector {
    val cache = mutableMapOf<Class<*>, EventCache>()

    fun apply(bus: EventBus, obj: Any) {
        cache.getOrPut(obj.javaClass) { EventCache(obj.javaClass) }.events.forEach { event ->
            @Suppress("UNCHECKED_CAST")
            bus.hook(
                event.type,
                event.priority,
                event.receiveCanceled
            ) { event.method.callFast(obj, it) }
        }
    }

    private val eventMirror = Mirror.reflectClass<Event>()

    class EventCache(clazz: Class<*>) {
        val events: List<HookCache>

        init {
            val events = mutableListOf<HookCache>()

            generateSequence(Mirror.reflectClass(clazz)) { it.superclass }
                .flatMap { it.declaredMethods.asSequence() }
                .forEach { method ->
                    val hookAnnotation = method.annotations.filterIsInstance<Hook>().firstOrNull() ?: return@forEach
                    val eventType = method.parameterTypes.singleOrNull()
                        ?: throw IllegalStateException("Invalid @Hook method '$method' in class " +
                            "'${method.declaringClass}'. Hook methods must have only one parameter")
                    if (!eventMirror.isAssignableFrom(eventType)) {
                        throw IllegalStateException("Invalid @Hook method '$method' in class " +
                            "'${method.declaringClass}'. Hook method parameters must be a subclass of Event")
                    }

                    @Suppress("UNCHECKED_CAST")
                    events.add(HookCache(
                        eventType.erasure as Class<Event>,
                        method,
                        hookAnnotation.priority,
                        hookAnnotation.receiveCanceled
                    ))
                }

            this.events = events
        }

        data class HookCache(
            val type: Class<Event>,
            val method: MethodMirror,
            val priority: EventBus.Priority,
            val receiveCanceled: Boolean
        )
    }
}

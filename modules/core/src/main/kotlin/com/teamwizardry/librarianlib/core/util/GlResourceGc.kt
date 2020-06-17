/*
 * Copyright (C) 2011-2020 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.teamwizardry.librarianlib.core.util

import org.apache.logging.log4j.LogManager
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import kotlin.reflect.KProperty

// based on code from javacpp:
// https://github.com/bytedeco/javacpp/blob/6aee52e/src/main/java/org/bytedeco/javacpp/Pointer.java

/**
 * Tracks (most often GL) resources and their associated objects, calling release functions after they have been GC'd.
 * The amount of time between the object being garbage collected and the release function is indeterminate, possibly
 * requiring multiple GC cycles.
 */
object GlResourceGc {
    /**
     * Starts tracking the given object, calling the [releaseFunction] on the main thread after [obj] is GC'd.
     *
     * The passed function _MUST NOT_ capture the tracked object. Doing so would lead to a circular reference and the
     * resource will leak. If you need any fields from the tracked object, put the values in variables outside the
     * function and use those.
     *
     * Bad:
     * ```java
     * public class MyGlThing {
     *     private int glHandle;
     *     public void foo() {
     *         GlResourceGc.track(this, () -> {
     *             GlXX.glDeleteXX(this.glHandle); // `this` is captured here so we can access the `glHandle` field,
     *                                             // so this resource will never be released.
     *         });
     *     }
     * }
     * ```
     * Good:
     * ```java
     * public class MyGlThing {
     *     private int glHandle;
     *     public void foo() {
     *         final int _glHandle = this.glHandle;
     *         GlResourceGc.track(this, () -> {
     *             GlXX.glDeleteXX(_glHandle); // only the `_glHandle` variable is captured here, not `this`
     *         });
     *     }
     * }
     * ```
     */
    @JvmStatic
    fun track(obj: Any, releaseFunction: Runnable): ResourceTracker {
        val ref = ResourceReference(obj, releaseFunction)
        ref.add()
        return ref
    }

    /**
     * Starts tracking the given object, calling the [releaseFunction] on the main thread when [obj] is GC'd.
     *
     * The passed function _MUST NOT_ capture the tracked object. Doing so would lead to a circular reference and the
     * resource will leak. If you need any fields from the tracked object, put the values in variables outside the
     * function and use those. If you want mutable state between the tracked object and the release function, store that
     * state inside a [Value] object and use references to that.
     *
     * Bad:
     * ```java
     * public class MyGlThing {
     *     private int glHandle;
     *     public void foo() {
     *         GlResourceGc.track(this, () -> {
     *             GlXX.glDeleteXX(this.glHandle); // `this` is captured here so we can access the `glHandle` field,
     *                                             // so this resource will never be released.
     *         });
     *     }
     * }
     * ```
     * Good:
     * ```java
     * public class MyGlThing {
     *     private int glHandle;
     *     public void foo() {
     *         final int _glHandle = this.glHandle;
     *         GlResourceGc.track(this, () -> {
     *             GlXX.glDeleteXX(_glHandle); // only the `_glHandle` variable is captured here, not `this`
     *         });
     *     }
     * }
     * ```
     */
    @JvmSynthetic
    inline fun track(obj: Any, crossinline releaseFunction: () -> Unit): ResourceTracker {
        return track(obj, Runnable { releaseFunction() })
    }

    /**
     * A simple cell for holding mutable state between objects and their release functions
     */
    class Value<T>(initialValue: T) {
        @get:JvmName("get")
        @set:JvmName("set")
        var value: T = initialValue

        @JvmSynthetic
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return this.value
        }
        @JvmSynthetic
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = value
        }
    }

    /**
     * A tracked resource. This supports both manual reference counting and tracking using object GC
     */
    interface ResourceTracker {
        /**
         * Returns true if this resource has already been released.
         */
        val isReleased: Boolean

        /**
         * Explicitly releases this resource. Returns true if the resource was released, or false if the resource was
         * already released. This will still function after [untrack] has been called.
         */
        fun release(): Boolean

        /**
         * Stops tracking this resource for garbage collection.
         */
        fun untrack()
    }

    // implementation ==================================================================================================

    private val logger = LogManager.getLogger(GlResourceGc::class.java)
    private val referenceQueue: ReferenceQueue<Any> = ReferenceQueue()


    /**
     * Releases all GC'd resources.
     *
     * **INTERNAL USE ONLY**
     */
    fun releaseCollectedResources() {
        generateSequence { referenceQueue.poll() as ResourceReference? }.forEach {
            it.clear()
            it.remove()
        }
    }

    /**
     * A subclass of [PhantomReference] that also acts as a linked list to keep their references alive until they get
     * garbage collected.
     */
    private class ResourceReference(p: Any, var releaseFunction: Runnable?): PhantomReference<Any>(p, referenceQueue), ResourceTracker {
        @Volatile
        var prev: ResourceReference? = null
        @Volatile
        var next: ResourceReference? = null

        override val isReleased: Boolean get() = releaseFunction == null

        /**
         * Disables this reference
         */
        var disable = false

        fun add() {
            synchronized(ResourceReference::class.java) {
                if (head == null) {
                    head = this
                } else {
                    next = head
                    head = this
                    next!!.prev = head
                }
            }
        }

        fun remove() {
            synchronized(ResourceReference::class.java) {
                if (prev === this && next === this) {
                    return
                }
                if (prev == null) {
                    head = next
                } else {
                    prev!!.next = next
                }
                if (next != null) {
                    next!!.prev = prev
                }
                next = this
                prev = next
            }
        }

        override fun untrack() {
            this.remove()
            this.disable = true
        }

        override fun clear() {
            super.clear()
            val releaseFunction = releaseFunction
            this.releaseFunction = null

            if (releaseFunction != null && !disable) {
//                if (logger.isDebugEnabled) {
                    logger.info("Collecting $releaseFunction")
//                }
                releaseFunction.run()
            }
        }

        override fun release(): Boolean {
            val releaseFunction = releaseFunction
            this.releaseFunction = null

            if (releaseFunction != null) {
                if (logger.isDebugEnabled) {
                    logger.debug("Releasing $this")
                }
                releaseFunction.run()
                return true
            }
            return false
        }

        companion object {
            @Volatile
            var head: ResourceReference? = null
        }
    }
}
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

import com.teamwizardry.librarianlib.core.LibLibCore
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.util.function.Consumer
import kotlin.reflect.KProperty

// based on code from javacpp:
// https://github.com/bytedeco/javacpp/blob/6aee52e/src/main/java/org/bytedeco/javacpp/Pointer.java

/**
 * Tracks (most often GL) resources and their associated objects, calling release functions after they have been GC'd.
 * The amount of time between the object being garbage collected and the release function is indeterminate, possibly
 * requiring multiple GC cycles.
 */
public object GlResourceGc {
    /**
     * Starts tracking the given object, calling the [releaseFunction] on the main thread at some point after [obj] is
     * garbage collected.
     *
     * The passed function _MUST NOT_ capture the tracked object. Doing so would lead to a circular reference and the
     * resource will leak. If you need any fields from the tracked object, put the values in variables outside the
     * function and use those.
     *
     * To help with this, the returned resource object can hold some mutable state which will be passed to the release
     * function.
     *
     * Bad:
     * ```java
     * public class MyGlThing {
     *     private int glHandle;
     *     public MyGlThing() {
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
     *     private GlResourceGc.Resource<Integer> glHandle;
     *     public MyGlThing() {
     *         this.glHandle = GlResourceGc.track(this, 10, (Integer state) -> {
     *             GlXX.glDeleteXX(state);
     *         });
     *
     *         // later in the code...
     *         this.glHandle.setState(100);
     *     }
     * }
     * ```
     */
    @JvmStatic
    public fun <T> track(obj: Any, state: T, releaseFunction: Consumer<T>): Resource<T> {
        val ref = ResourceReference(obj, state, releaseFunction)
        ref.add()
        return ref
    }

    /**
     * A tracked resource. This supports both manual reference counting and tracking using object GC
     */
    public interface Resource<S> {
        /**
         * Some shared state accessible within the release function. This value will not be cleared when the tracked
         * resource is released.
         */
        public var state: S

        /**
         * Returns true if this resource has already been released.
         */
        public val isReleased: Boolean

        /**
         * Explicitly releases this resource. Returns true if the resource was released, or false if the resource was
         * already released. This will still function after [untrack] has been called.
         */
        public fun release(): Boolean

        /**
         * Stops tracking this resource for garbage collection.
         */
        public fun untrack()

        @JvmSynthetic
        public operator fun getValue(thisRef: Any?, property: KProperty<*>): S {
            return this.state
        }

        @JvmSynthetic
        public operator fun setValue(thisRef: Any?, property: KProperty<*>, value: S) {
            this.state = value
        }
    }

    // implementation ==================================================================================================

    private val referenceQueue: ReferenceQueue<Any> = ReferenceQueue()

    /**
     * Releases all GC'd resources.
     *
     * **INTERNAL USE ONLY**
     */
    public fun releaseCollectedResourcesInternal() {
        generateSequence { referenceQueue.poll() as ResourceReference<*>? }.forEach {
            it.clear()
            it.remove()
        }
    }

    /**
     * A subclass of [PhantomReference] that also acts as a linked list to keep their references alive until they get
     * garbage collected.
     */
    private class ResourceReference<T>(p: Any, override var state: T, var releaseFunction: Consumer<T>?): PhantomReference<Any>(p, referenceQueue), Resource<T> {
        @Volatile
        var prev: ResourceReference<*>? = null

        @Volatile
        var next: ResourceReference<*>? = null

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
                logger.debug("Collecting $releaseFunction")
                releaseFunction.accept(state)
            }
        }

        override fun release(): Boolean {
            val releaseFunction = releaseFunction
            this.releaseFunction = null

            if (releaseFunction != null) {
                logger.debug("Releasing $this")
                releaseFunction.accept(state)
                return true
            }
            return false
        }

        companion object {
            @Volatile
            var head: ResourceReference<*>? = null
        }
    }

    private val logger = LibLibCore.makeLogger<GlResourceGc>()
}
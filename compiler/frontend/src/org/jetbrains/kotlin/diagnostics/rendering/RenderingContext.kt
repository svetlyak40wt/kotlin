/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.diagnostics.rendering

sealed class RenderingContext {
    abstract fun <T> compute(key: Key<T>, computation: (Collection<Any?>) -> T): T

    class Key<T>(val name: String)

    class Impl(private val objectsToRender: Collection<Any?>) : RenderingContext() {
        private val data = linkedMapOf<Key<*>, Any?>()

        override fun <T> compute(key: Key<T>, computation: (Collection<Any?>) -> T): T {
            if (!data.containsKey(key)) {
                val result = computation(objectsToRender)
                data[key] = result
                return result
            }
            return data[key] as T
        }
    }


    object Empty : RenderingContext() {
        override fun <T> compute(key: Key<T>, computation: (Collection<Any?>) -> T): T {
            return computation(emptyList())
        }
    }
}
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

package org.jetbrains.kotlin.codegen.inline

abstract class RegenerationInfo() {

    abstract fun getOldClassName(): String

    abstract fun getNewClassName(): String

    abstract fun shouldRegenerate(sameModule: Boolean): Boolean

    abstract fun canRemoveAfterTransformation(): Boolean

}

class WhenMappingRegenerationInfo(val oldName: String, val nameGenerator: NameGenerator) : RegenerationInfo() {

    val newName by lazy {
        nameGenerator.genLambdaClassName() + oldName
    }

    override fun shouldRegenerate(sameModule: Boolean): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getOldClassName(): String {
        return oldName
    }

    override fun getNewClassName(): String {
        return newName
    }

    override fun canRemoveAfterTransformation(): Boolean {
        return true
    }
}
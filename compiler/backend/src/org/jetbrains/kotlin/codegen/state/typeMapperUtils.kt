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

package org.jetbrains.kotlin.codegen.state

import org.jetbrains.kotlin.codegen.OwnerKind
import org.jetbrains.kotlin.codegen.signature.BothSignatureWriter
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor


fun createSignatureWriter(function: FunctionDescriptor, kind: OwnerKind): BothSignatureWriter {
    if (kind != OwnerKind.DEFAULT_IMPLS) return BothSignatureWriter(BothSignatureWriter.Mode.METHOD)

    val classDescriptor = function.containingDeclaration as ClassDescriptor
    if (function.typeParameters.isNotEmpty() && classDescriptor.declaredTypeParameters.isNotEmpty()) {
        val functionTypeParameters = function.typeParameters.map { it.name.asString() }.toMutableSet()
        val interfaceTypeParameters = classDescriptor.declaredTypeParameters.map { it.name.asString() }
        val clashedInterfaceTypeParameters = interfaceTypeParameters.intersect(functionTypeParameters)

        if (clashedInterfaceTypeParameters.isNotEmpty()) {
            val mappingForInterfaceTypeParameters = clashedInterfaceTypeParameters.associateBy ({ it }) {
                typeParameterName ->
                var newNamePrefix = typeParameterName + "_I"
                var newName = newNamePrefix + generateSequence(1) { x -> x + 1 }.first { index -> newNamePrefix + index !in functionTypeParameters }
                functionTypeParameters.add(newName)
                newName
            }

            return BothSignatureWriter(BothSignatureWriter.Mode.METHOD) {
                typeParameterName ->
                mappingForInterfaceTypeParameters[typeParameterName] ?: typeParameterName
            }
        }
    }

    return BothSignatureWriter(BothSignatureWriter.Mode.METHOD)
}
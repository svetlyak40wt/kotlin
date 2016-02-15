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

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.renderer.NameShortness
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.contains
import java.util.*

class SmartTypeRenderer(private val baseRenderer: DescriptorRenderer) : SmartRenderer<KotlinType> {
    override fun render(obj: KotlinType, context: RenderingContext): String {

        val adaptiveRenderer = context.compute(KEY) { objects ->
            baseRenderer.withOptions {
                nameShortness = AdaptiveNameShortness(collectMentionedClassifiers(objects))
            }
        }

        return adaptiveRenderer.renderType(obj)
    }

    private fun collectMentionedClassifiers(objects: Collection<Any?>): Set<ClassifierDescriptor> {
        val result = LinkedHashSet<ClassifierDescriptor>()
        objects.filterIsInstance<KotlinType>().forEach { diagnosticType ->
            diagnosticType.contains {
                innerType ->
                innerType.constructor.declarationDescriptor?.let { result.add(it) }
                false
            }
        }
        return result
    }

    private class AdaptiveNameShortness(mentionedClassifiers: Set<ClassifierDescriptor>) : NameShortness {
        private val byName = mentionedClassifiers.groupBy { it.name }

        override fun renderClassifier(classifier: ClassifierDescriptor, renderer: DescriptorRenderer): String {
            val uniqueName = (byName[classifier.name]?.size ?: 0) <= 1
            return when {
                uniqueName -> NameShortness.SHORT.renderClassifier(classifier, renderer)
                classifier is ClassDescriptor -> NameShortness.FULLY_QUALIFIED.renderClassifier(classifier, renderer)
                classifier is TypeParameterDescriptor -> with (renderer) {
                    "${renderName(classifier.name)} ${renderMessage("(defined in ${renderFqName(classifier.containingDeclaration.fqNameUnsafe)})")}"
                }
                else -> error("Unexpected classifier: ${classifier.javaClass}")
            }
        }
    }

    companion object {
        private val KEY = RenderingContext.Key<DescriptorRenderer>("ADAPTIVE_TYPE_RENDERER")
    }
}
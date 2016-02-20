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

package org.jetbrains.kotlin.idea.spring.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.ElementDescriptionUtil
import com.intellij.psi.PsiElementVisitor
import com.intellij.refactoring.util.RefactoringDescriptionLocation
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptor
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isInheritable
import org.jetbrains.kotlin.psi.psiUtil.isOverridable

private val CONFIGURATION_ANNOTATION_FQ_NAME = FqName("org.springframework.context.annotation.Configuration")
private val COMPONENT_ANNOTATION_FQ_NAME = FqName("org.springframework.stereotype.Component")
private val BEAN_ANNOTATION_FQ_NAME = FqName("org.springframework.context.annotation.Bean")

class FinalKotlinClassOrFunSpringInspection : AbstractKotlinInspection() {
    class QuickFix<T: KtModifierListOwner>(private val element: T) : LocalQuickFix {
        override fun getName(): String {
            return "Make ${ElementDescriptionUtil.getElementDescription(element, RefactoringDescriptionLocation.WITHOUT_PARENT)} open"
        }

        override fun getFamilyName() = name

        override fun applyFix(project: Project, problemDescriptor: ProblemDescriptor) {
            element.addModifier(KtTokens.OPEN_KEYWORD)
        }
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object: KtVisitorVoid() {
            override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
                when (declaration) {
                    is KtClass -> if (declaration.isInheritable()) return
                    is KtNamedFunction -> if (declaration.isOverridable()) return
                    else -> return
                }

                val annotations = declaration.resolveToDescriptor().annotations
                val message = when {
                    annotations.hasAnnotation(CONFIGURATION_ANNOTATION_FQ_NAME) -> "@Configuration class should be declared open"
                    annotations.hasAnnotation(COMPONENT_ANNOTATION_FQ_NAME) -> "@Component class should be declared open"
                    annotations.hasAnnotation(BEAN_ANNOTATION_FQ_NAME) -> "@Bean function should be declared open"
                    else -> return
                }
                holder.registerProblem(
                        declaration.nameIdentifier ?: declaration,
                        message,
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        QuickFix(declaration)
                )
            }
        }
    }
}
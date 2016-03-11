/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

import java.util.*

class AnonymousObjectRegenerationInfo internal constructor(
		private val ownerInternalName: String,
		private val needReification: Boolean,
		val lambdasToInline: Map<Int, LambdaInfo>,
		private val capturedOuterRegenerated: Boolean,
		private val alreadyRegenerated: Boolean,
		val constructorDesc: String?,
		private val isStaticOrigin: Boolean,
		private val nameGenerator: NameGenerator) : RegenerationInfo() {

	private val newLambdaType: String by lazy {
		nameGenerator.genLambdaClassName()
	}

	var newConstructorDescriptor: String? = null

	var allRecapturedParameters: List<CapturedParamDesc>? = null

	var capturedLambdasToInline: Map<String, LambdaInfo>? = null

	constructor(
			ownerInternalName: String,
			needReification: Boolean,
			alreadyRegenerated: Boolean,
			isStaticOrigin: Boolean,
			nameGenerator: NameGenerator
	) : this(
			ownerInternalName, needReification,
			HashMap<Int, LambdaInfo>(), false, alreadyRegenerated, null, isStaticOrigin, nameGenerator) {
	}

	override fun getOldClassName(): String {
		return ownerInternalName
	}

	override fun shouldRegenerate(sameModule: Boolean): Boolean {
		return !alreadyRegenerated && (!lambdasToInline.isEmpty() || !sameModule || capturedOuterRegenerated || needReification)
	}

	override fun canRemoveAfterTransformation(): Boolean {
		// Note: It is unsafe to remove anonymous class that is referenced by GETSTATIC within lambda
		// because it can be local function from outer scope
		return !isStaticOrigin
	}

	override fun getNewClassName(): String {
		return newLambdaType
	}
}

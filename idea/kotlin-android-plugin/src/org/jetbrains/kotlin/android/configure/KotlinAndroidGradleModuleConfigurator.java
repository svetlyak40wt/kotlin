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

package org.jetbrains.kotlin.android.configure;

import com.android.tools.idea.gradle.GradleSyncState;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinPluginUtil;
import org.jetbrains.kotlin.idea.configuration.KotlinWithGradleConfigurator;
import org.jetbrains.kotlin.resolve.TargetPlatform;
import org.jetbrains.kotlin.resolve.jvm.platform.JvmPlatform;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock;

public class KotlinAndroidGradleModuleConfigurator extends KotlinWithGradleConfigurator {
    public static final String NAME = "android-gradle";

    private static final String APPLY_KOTLIN_ANDROID = "apply plugin: 'kotlin-android'";

    @Override
    public boolean isConfigured(@NotNull Module module) {
        return GradleSyncState.getInstance(module.getProject()).isSyncInProgress() || super.isConfigured(module);
    }

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @NotNull
    @Override
    public TargetPlatform getTargetPlatform() {
        return JvmPlatform.INSTANCE;
    }

    @NotNull
    @Override
    public String getPresentableText() {
        return "Android with Gradle";
    }

    @Override
    public boolean isApplicable(@NotNull Module module) {
        return KotlinPluginUtil.isAndroidGradleModule(module);
    }

    @Override
    protected String getApplyPluginDirective() {
        return APPLY_KOTLIN_ANDROID;
    }

    @Override
    protected void addSourceSetsBlock(@NotNull GroovyFile file) {
        GrClosableBlock androidBlock = getAndroidBlock(file);
        KotlinWithGradleConfigurator.addLastExpressionInBlockIfNeeded(KotlinWithGradleConfigurator.SOURCE_SET, KotlinWithGradleConfigurator
                .getSourceSetsBlock(androidBlock));
    }

    @Override
    protected boolean addElementsToFile(@NotNull GroovyFile groovyFile, boolean isProjectFile, @NotNull String version) {
        if (isProjectFile) {
            addElementsToProjectFile(groovyFile, version);
        }
        else {
            addElementsToModuleFile(groovyFile, version);
        }
        return true;
    }

    @NotNull
    private static GrClosableBlock getAndroidBlock(@NotNull GroovyFile file) {
        return KotlinWithGradleConfigurator.getBlockOrCreate(file, "android");
    }

    KotlinAndroidGradleModuleConfigurator() {
    }
}

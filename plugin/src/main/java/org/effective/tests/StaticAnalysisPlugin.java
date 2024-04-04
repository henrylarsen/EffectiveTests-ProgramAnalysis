package org.effective.tests;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.util.Collections;
import java.util.List;

public class StaticAnalysisPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        String injectedSrcDir = project.getProjectDir() + "/build/injectedTestSrc/";
        String srcDir = project.getProjectDir() + "/src/";
        Task analyzeTask = project.getTasks().register("analyze", AnalyzeTask.class, (task) -> {
            task.setGroup("Static Analysis");
            task.setDescription("Runs comprehensive test analysis on this project's source code");
            task.setPaths(srcDir, injectedSrcDir);
        }).get();

        // We want our task to run before compileJava, but only when the main gradle task was test
        List<String> taskNames = project.getGradle().getStartParameter().getTaskNames();
        String taskName = taskNames.size() > 0 ? taskNames.get(0) : "";
        int i = taskName.lastIndexOf(':');
        if (i >= 0 && taskName.substring(i).equals(":test")) {
            project.afterEvaluate(p -> {
                Task compileJavaTask = project.getTasks().findByName("compileJava");
                if (compileJavaTask != null) {
                    // Tell :analyze to run before :compileJava
                    compileJavaTask.dependsOn(analyzeTask);
                }

                // Tell this project to refer to injectedSrc as the java code source
                SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
                tryOverrideSourceSet(sourceSets, injectedSrcDir, "main");
                tryOverrideSourceSet(sourceSets, injectedSrcDir, "test");
            });
        }
    }

    public void tryOverrideSourceSet(SourceSetContainer container, String targetDirectory, String setName) {
        try {
            SourceSet mainSourceSet = container.getByName(setName);
            mainSourceSet.getJava().setSrcDirs(Collections.singleton(targetDirectory + setName + "/java"));
        } catch (UnknownDomainObjectException e) {
        }
    }

}

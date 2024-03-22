package org.effective.tests;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class StaticAnalysisPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().register("analyze", AnalyzeTask.class);
        Task analysisTask = project.getTasksByName("analyze", false).iterator().next();
        analysisTask.setGroup("Static Analysis");
        analysisTask.setDescription("Runs comprehensive test analysis on this project's source code");
    }
}

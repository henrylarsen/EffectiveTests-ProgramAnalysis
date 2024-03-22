package org.effective.tests;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public abstract class AnalyzeTask extends DefaultTask {

    @TaskAction
    public void analyze() {
        AnalysisRunner runner = new AnalysisRunner();
        runner.run();
    }
}

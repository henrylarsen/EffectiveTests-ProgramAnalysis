package org.effective.tests;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public abstract class AnalyzeTask extends DefaultTask {
    String targetPath = "";
    String sourcePath = "";
    public void setPaths(String sourcePath, String outputPath) {
        this.sourcePath = sourcePath;
        this.targetPath = outputPath;
    }
    @TaskAction
    public void analyze() {
        System.out.println("Running analyzer. Will output files to: " + targetPath);
        AnalysisRunner runner = new AnalysisRunner();
        runner.run(sourcePath, targetPath);
    }
}

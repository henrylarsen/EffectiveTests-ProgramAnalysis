package org.effective.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnalyzeTaskTest {
    @Test
    void testAnalyze() {
        AnalysisRunner runner = new AnalysisRunner();
        String result = runner.run();
        assertEquals(result, "ran");
    }
}

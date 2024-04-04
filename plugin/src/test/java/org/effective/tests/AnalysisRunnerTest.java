package org.effective.tests;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AnalysisRunnerTest {
    private AnalysisRunner ar;
    private static ClassCollector cc;
    private static final String DIR_PATH = "src/test/java/org/effective/tests";

    private static final Path testPath = Paths.get(DIR_PATH + "/data/FModdersTest.java");

    private static final String sourceClassName = "FModders";

    private static final String testClassName = "FModdersTest";

    @BeforeAll
    static void init() {
        cc = new ClassCollector();
    }

    @Test
    public void testClassCollection() {
        cc.collectClasses(DIR_PATH);
        Map<Path, TestData> testClasses = cc.getTestClassData();
        assertTrue(testClasses.size() == 1);
        assertTrue(testClasses.containsKey(testPath));
        TestData testData = testClasses.get(testPath);
        CompilationUnit testClass = testData.getTestClass();
        assertNotNull(testClass);
        assertNotNull(testClass.getClassByName(testClassName));

        assertEquals(testData.getSourceClassName(), "FModders");

        Map<String, CompilationUnit> sourceClasses = cc.getSourceClasses();
        assertTrue(sourceClasses.size() == 1);

        CompilationUnit sourceCode = sourceClasses.get(sourceClassName);
        assertNotNull(sourceCode);
        assertNotNull(sourceCode.getClassByName(sourceClassName));
    }
}
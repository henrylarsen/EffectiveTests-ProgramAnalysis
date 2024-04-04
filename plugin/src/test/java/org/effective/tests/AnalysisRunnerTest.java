package org.effective.tests;

import com.github.javaparser.ast.CompilationUnit;
import org.effective.tests.effects.MethodData;
import org.effective.tests.staticVariables.VarType;
import org.effective.tests.visitors.AnalyzeVisitor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AnalysisRunnerTest {
    private AnalysisRunner ar;
    private static ClassCollector cc;
    private static final String DIR_PATH = "src/test/java/org/effective/tests";

    private static final Path testPath = Paths.get(DIR_PATH + "/data/FModdersTest.java");

    private static final String sourceClassName1 = "FModders";
    private static final String sourceClassName2 = "AMod";

    private static final String testClassName1 = "FModdersTest";
    private static final String testClassName2 = "AModTest";

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
        assertNotNull(testClass.getClassByName(testClassName1));

        assertEquals(testData.getSourceClassName(), "FModders");

        Map<String, CompilationUnit> sourceClasses = cc.getSourceClasses();
        assertTrue(sourceClasses.size() == 2);

        CompilationUnit sourceCode = sourceClasses.get(sourceClassName1);
        assertNotNull(sourceCode);
        assertNotNull(sourceCode.getClassByName(sourceClassName1));
    }

    @Test
    public void testCollectionStaticAnalysis() {
        cc.collectClasses(DIR_PATH);
        Map<Path, TestData> testClasses = cc.getTestClassData();
        Map<String, CompilationUnit> sourceClasses = cc.getSourceClasses();

        Map<String, Map<MethodData, Set<VarType>>> results = ar.analyzeTests(sourceClasses, testClasses);
        assertEquals(2, results.size());
        assertNotNull(results.get(sourceClassName1));
        assertNotNull(results.get(sourceClassName2));
        assertEquals(8, results.get(sourceClassName1).size());
        assertEquals(7, results.get(sourceClassName2).size());
        // these two methods appear in two different files
        assertNotNull(results.get(sourceClassName1).get(new MethodData("sendFour", new ArrayList<>(), 0)));
        assertNotNull(results.get(sourceClassName1).get(new MethodData("sendThree", new ArrayList<>(), 0)));
    }
}
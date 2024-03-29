package org.effective.tests;


import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.effective.tests.effects.Field;
import org.effective.tests.effects.MethodData;
import org.effective.tests.visitors.*;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnalyzeTest {
    private static final String DIR_PATH = "src/test/java/org/effective/tests/data/";
    @Test
    void testFileFModders() {
        try {
            StaticJavaParser.getConfiguration().setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver()));
            CompilationUnit cuClass = StaticJavaParser.parse(new File(DIR_PATH + "FModders.java"));
            CompilationUnit cuTest = StaticJavaParser.parse(new File(DIR_PATH + "FModdersTest.java"));
            VarCollector fieldCollector = new VarCollector();
            VarContext fields = fieldCollector.collectVars(cuClass);
            EffectCollector effectCollector = new EffectCollector();
            effectCollector.collectEffects(cuClass, fields);
            AnalyzeVisitor av = new AnalyzeVisitor(effectCollector.getCtx());
            Map<MethodData, Set<Field>> results = av.analyzeTest(cuTest, "FModders");

            MethodData md1 = new MethodData("getA", new ArrayList<>(), 0);
            MethodData md2 = new MethodData("setA", List.of("int"), 0);
            MethodData md3 = new MethodData("foo", List.of("int"), 0);
            assertTrue(results.containsKey(md1));
            assertTrue(results.containsKey(md2));
            assertTrue(results.containsKey(md3));
            assertEquals(0, results.get(md1).size());
            assertTrue(Set.of(new Field("a")).equals(results.get(md2)));
            assertTrue(Set.of(new Field("a")).equals(results.get(md3)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}

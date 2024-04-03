package org.effective.tests;


import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.effective.tests.effects.MethodData;
import org.effective.tests.modifier.FileWriterWrapper;
import org.effective.tests.staticVariables.VarClassField;
import org.effective.tests.staticVariables.VarMethodReturn;
import org.effective.tests.staticVariables.VarType;
import org.effective.tests.visitors.*;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
            Map<MethodData, Set<VarType>> results = av.analyzeTest(cuTest, "FModders");

            MethodData getA = new MethodData("getA", new ArrayList<>(), 0);
            MethodData setA = new MethodData("setA", List.of("int"), 0);
            MethodData foo = new MethodData("foo", List.of("int"), 0);
            MethodData sendThree = new MethodData("sendThree", new ArrayList<>(), 0);
            assertTrue(results.containsKey(getA));
            assertTrue(results.containsKey(setA));
            assertTrue(results.containsKey(foo));
            assertTrue(results.containsKey(sendThree));
            assertEquals(Set.of(new VarMethodReturn("fm1", getA)),results.get(getA));
            assertEquals(Set.of(new VarClassField("fm1", "a")), results.get(setA));
            assertEquals(Set.of(new VarClassField("fm1", "a")), results.get(foo));
            assertEquals(Set.of(new VarMethodReturn("fm1", sendThree)), results.get(sendThree));

            FileWriterWrapper fw = null;
            try {
                fw = new FileWriterWrapper(DIR_PATH + "/inject/FModdersTest.java");
                fw.write(cuTest.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}

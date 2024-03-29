package org.effective.tests;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.utils.Pair;
import org.effective.tests.effects.Effect;
import org.effective.tests.effects.Field;
import org.effective.tests.effects.MethodData;
import org.effective.tests.effects.Return;
import org.effective.tests.modifier.EffectInjectionModifier;
import org.effective.tests.modifier.FileWriterWrapper;
import org.effective.tests.visitors.EffectCollector;
import org.effective.tests.visitors.VarCollector;
import org.effective.tests.visitors.VarContext;
import org.junit.jupiter.api.BeforeAll;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class EffectInjectionModifierTest {
    private static final String DIR_PATH = "src/test/java/org/effective/tests/data";
    private static EffectInjectionModifier effectInjectionModifier;
    private VarCollector varCollector;
    private static EffectCollector effectCollector;
    private VarContext vars;


    @BeforeAll
    static void setUp() {
        effectCollector = new EffectCollector();
    }

    private Map<MethodData, List<Effect>> collectFields(CompilationUnit cu) {
        varCollector = new VarCollector();
        vars = varCollector.collectVars(cu);
        effectCollector = new EffectCollector();
        return effectCollector.collectEffects(cu, vars);
    }

    private CompilationUnit getUnit(String fileName) throws IOException {
        return StaticJavaParser.parse(Files.newInputStream(Paths.get(DIR_PATH + fileName)));
    }


    @Test
    void singleReturnStmt() {
        try {
            CompilationUnit cu = getUnit("/Return.java");
            effectInjectionModifier = new EffectInjectionModifier(collectFields(cu));
            effectInjectionModifier.visit(cu, null);
            System.out.println(cu.toString());

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void fieldModsStmt() {
        try {
            CompilationUnit cu = getUnit("/FieldMods.java");
            effectInjectionModifier = new EffectInjectionModifier(collectFields(cu));
            effectInjectionModifier.visit(cu, null);
            FileWriterWrapper fw = new FileWriterWrapper(DIR_PATH + "/inject/FieldMods.java");
            fw.write(cu.toString());

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void maxTrackerTest() {
        try {
            CompilationUnit cu = getUnit("/MaxTracker.java");
            effectInjectionModifier = new EffectInjectionModifier(collectFields(cu));
            effectInjectionModifier.visit(cu, null);
            FileWriterWrapper fw = new FileWriterWrapper(DIR_PATH + "/inject/MaxTracker.java");
            fw.write(cu.toString());

        } catch (IOException e) {
            fail(e);
        }
    }

}

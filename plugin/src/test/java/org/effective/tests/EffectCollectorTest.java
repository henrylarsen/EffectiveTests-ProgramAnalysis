package org.effective.tests;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.effective.tests.effects.Effect;
import org.effective.tests.effects.Field;
import org.effective.tests.effects.Modification;
import org.effective.tests.effects.Return;
import org.effective.tests.visitors.FieldCollector;
import org.effective.tests.visitors.EffectContext;
import org.effective.tests.visitors.EffectCollector;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EffectCollectorTest {

    private static final String DIR_PATH = "src/test/java/org/effective/tests/data";
    private FieldCollector fieldCollector;
    private static EffectCollector effectCollector;
    private Set<Field> fields;
    private EffectContext ctx;


    @BeforeAll
    static void setUp() {
        effectCollector = new EffectCollector();
    }

    private CompilationUnit getUnit(String fileName) throws IOException {
        return StaticJavaParser.parse(Files.newInputStream(Paths.get(DIR_PATH + fileName)));
    }

    private EffectContext collectFields(CompilationUnit cu) {
        fieldCollector = new FieldCollector();
        fields = fieldCollector.collectFields(cu);
        effectCollector = new EffectCollector();
        effectCollector.collectEffects(cu, fields);
        return effectCollector.getCtx();
    }

    @Test
    void singleReturnStmt() {
        try {
            CompilationUnit cu = getUnit("/Return.java");
            ctx = collectFields(cu);

            List<Effect> testableEffects = ctx.getAllTestableEffects();
            assertEquals(ctx.getAllEffects().size(), 1);
            assertTrue(testableEffects.contains(new Return("getX", 10)));

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void setField() {
        try {
            CompilationUnit cu = getUnit("/FieldMods.java");
            ctx = collectFields(cu);

            List<Effect> testableEffects = ctx.getAllTestableEffects();
            assertEquals(ctx.getAllEffects().size(), 7);
            assertEquals(testableEffects.size(), 5);

            Field a = new Field("a", true);
            Field b = new Field("b");
            Field c = new Field("c", true);

            assertTrue(testableEffects.contains(new Return("getA", 13)));
            assertTrue(testableEffects.contains(new Return("getC", 17)));
            assertTrue(testableEffects.contains(new Modification("setA", 21, a)));
            assertTrue(testableEffects.contains(new Modification("foo", 26, a)));
            assertTrue(testableEffects.contains(new Modification("foo", 27, c)));

            assertFalse(testableEffects.contains(new Modification("setB", 34, b)));
            assertFalse(testableEffects.contains(new Modification("foo", 29, b)));

            assertTrue(ctx.getFields().size() == 3);
            assertTrue(ctx.getField("a").isAvailable());
            assertFalse(ctx.getField("b").isAvailable());
            assertTrue(ctx.getField("c").isAvailable());

        } catch (IOException e) {
            fail(e);
        }
    }
}

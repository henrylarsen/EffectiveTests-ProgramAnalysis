package org.effective.tests;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;

import org.effective.tests.effects.Field;
import org.effective.tests.effects.Modification;
import org.effective.tests.effects.Return;
import org.effective.tests.visitors.ProgramContext;
import org.effective.tests.visitors.EffectsCollector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class EffectsCollectorTest {

    private static final String DIR_PATH = "src/test/java/org/effective/tests/data";
    private ProgramContext ctx;
    private VoidVisitor<ProgramContext> v;

    private CompilationUnit getUnit(String fileName) throws IOException {
        return StaticJavaParser.parse(Files.newInputStream(Paths.get(DIR_PATH + fileName)));
    }

    @BeforeEach
    void setUp() {
        ctx = new ProgramContext();
        v = new EffectsCollector();
    }

    @Test
    void singleReturnStmt() {
        try {
            CompilationUnit cu = getUnit("/Return.java");
            cu.accept(v, ctx);
            assertEquals(ctx.getEffectMap().size(), 1);
            assertTrue(ctx.containsEffect(new Return("getX", 10)));

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void setField() {
        try {
            CompilationUnit cu = getUnit("/FieldMods.java");
            cu.accept(v, ctx);

            assertEquals(ctx.getEffectMap().size(), 4);
            assertEquals(ctx.getAllEffects().size(), 5);

            Field a = new Field("a");
            Field b = new Field("b");
            Field c = new Field("c");

            assertTrue(ctx.containsEffect(new Return("getA", 13)));
            assertTrue(ctx.containsEffect(new Return("getC", 17)));
            assertTrue(ctx.containsEffect(new Modification("setA", 21, a)));
            assertTrue(ctx.containsEffect(new Modification("foo", 26, a)));
            assertTrue(ctx.containsEffect(new Modification("foo", 27, c)));

            assertFalse(ctx.containsEffect(new Modification("setB", 34, b)));
            assertFalse(ctx.containsEffect(new Modification("foo", 29, b)));

            assertTrue(ctx.getFields().size() == 3);
            assertTrue(ctx.getField("a").isAvailable());
            assertFalse(ctx.getField("b").isAvailable());
            assertTrue(ctx.getField("c").isAvailable());

        } catch (IOException e) {
            fail(e);
        }
    }
}

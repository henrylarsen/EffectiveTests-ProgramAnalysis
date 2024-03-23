package org.effective.tests;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.effective.tests.visitors.ProgramContext;
import org.effective.tests.visitors.EffectsCollector;
import org.effective.tests.visitors.MethodDetective;
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
    private MethodDetective md;

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
            assertEquals(ctx.getEffects().size(), 1);
            /*MethodDeclaration m = md.findMethodByName(cu, "getX");
            BlockStmt b = (BlockStmt) m.getChildNodes().iterator().next();
            List<Effect> lst = ctx.get(new BlockStmtWrapper(b));
            System.out.println(lst);*/

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void setField() {
        try {
            CompilationUnit cu = getUnit("/FieldMods.java");
            cu.accept(v, ctx);
            assertEquals(ctx.getEffects().size(), 2);
            System.out.println("CTX: " + ctx);
        } catch (IOException e) {
            fail(e);
        }
    }
}

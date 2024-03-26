package org.effective.tests;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.Pair;
import org.effective.tests.effects.Field;
import org.effective.tests.visitors.VarCollector;
import org.effective.tests.visitors.VarContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class VarCollectorTest {
    private static final String DIR_PATH = "src/test/java/org/effective/tests/data";
    private static VarCollector varCollector;
    private VarContext vars;

    private CompilationUnit getUnit(String fileName) throws IOException {
        return StaticJavaParser.parse(Files.newInputStream(Paths.get(DIR_PATH + fileName)));
    }

    @BeforeAll
    public static void initialize()
    {
        varCollector = new VarCollector();
    }

    private Set<Field> getAvailableFields(Set<Field> fields) {
        return fields.stream().filter(f -> f.isAvailable()).collect(Collectors.toSet());
    }

    @Test
    void getFields() {
        try {
            CompilationUnit cu = getUnit("/FieldMods.java");
            vars = varCollector.collectVars(cu);
            Set<Field> fields = vars.getFields();
            assertEquals(fields.size(), 3);
            assertEquals(getAvailableFields(fields).size(), 2);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void getFieldsOutOfOrder() {
        try {
            CompilationUnit cu = getUnit("/OutOfOrder.java");
            vars = varCollector.collectVars(cu);
            Set<Field> fields = vars.getFields();
            assertEquals(fields.size(), 1);
            assertEquals(getAvailableFields(fields).size(), 1);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void noGetter() {
        try {
            CompilationUnit cu = getUnit("/WithCounter.java");
            vars = varCollector.collectVars(cu);
            Set<Field> fields = vars.getFields();
            assertEquals(fields.size(), 1);
            assertEquals(getAvailableFields(fields).size(), 0);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void localVars() {
        try {
            CompilationUnit cu = getUnit("/Duplication.java");
            vars = varCollector.collectVars(cu);

            Set<Field> fields = vars.getFields();
            assertEquals(fields.size(), 2);
            assertEquals(getAvailableFields(fields).size(), 2);

            Map<Pair<String, Integer>, List<String>> localVars = vars.getLocalVars();
            assertEquals(localVars.size(), 1);
            List<String> barVars = localVars.get(new Pair("bar", 5));
            assertTrue(barVars.contains("x"));
            assertTrue(barVars.contains("y"));

        } catch (IOException e) {
            fail(e);
        }
    }
}

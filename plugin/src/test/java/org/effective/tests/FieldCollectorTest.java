package org.effective.tests;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.effective.tests.effects.Field;
import org.effective.tests.visitors.FieldCollector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FieldCollectorTest {
    private static final String DIR_PATH = "src/test/java/org/effective/tests/data";
    private static FieldCollector fieldCollector;
    private Set<Field> fields;

    private CompilationUnit getUnit(String fileName) throws IOException {
        return StaticJavaParser.parse(Files.newInputStream(Paths.get(DIR_PATH + fileName)));
    }

    @BeforeAll
    public static void initialize()
    {
        fieldCollector = new FieldCollector();
    }

    private Set<Field> getAvailableFields(Set<Field> fields) {
        return fields.stream().filter(f -> f.isAvailable()).collect(Collectors.toSet());
    }

    @Test
    void getFields() {
        try {
            CompilationUnit cu = getUnit("/FieldMods.java");
            fields = fieldCollector.collectFields(cu);
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
            fields = fieldCollector.collectFields(cu);
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
            fields = fieldCollector.collectFields(cu);
            assertEquals(fields.size(), 1);
            assertEquals(getAvailableFields(fields).size(), 0);
        } catch (IOException e) {
            fail(e);
        }
    }
}

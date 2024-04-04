package org.effective.tests;

import com.github.javaparser.ast.CompilationUnit;
import org.effective.tests.effects.Effect;
import org.effective.tests.effects.MethodData;
import org.effective.tests.modifier.EffectInjectionModifier;
import org.effective.tests.modifier.FileWriterWrapper;
import org.effective.tests.visitors.EffectCollector;
import org.effective.tests.visitors.VarCollector;
import org.effective.tests.visitors.VarContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class AnalysisRunner {

    public String run(String sourcePath, String targetPath) {
        System.out.println("Starting analysis...");
        prepareAnalysisDirectory(sourcePath, targetPath);
        // TODO: Complete this rough outline of steps:

        // Crawl targetPath to collect annotated test files and their files under test that need injection
        ClassCollector cc = new ClassCollector();
        cc.collectClasses(targetPath);

        // For each file under test, analyze code for effects and perform injections accordingly
        Map<String, CompilationUnit> sourceClasses = cc.getSourceClasses();

        // For each test file, analyze code for effect assertions and perform injections accordingly
        Map<Path, TestData> testData = cc.getTestClassData();

        /* For Ron: you can iterate through the map and call
        e.getValue().getSourceClassName(), which returns a string (to be your targetClass),
        and e.getValue().getTestClass(), which returns a CompilationUnit,
        for your analysis
         */

        System.out.println("Sources: " + sourceClasses.entrySet());

        System.out.println("Tests: " + testData.entrySet());

        sourceInjection(sourceClasses, targetPath);

        // Inject code to produce results, likely as an afterAll of some sort

        return "ran";
    }

    private void sourceInjection(Map<String, CompilationUnit> sourceClasses, String targetPath) {

        for (String key: sourceClasses.keySet()) {
            CompilationUnit cu = sourceClasses.get(key);
            VarCollector varCollector = new VarCollector();
            EffectCollector effectCollector = new EffectCollector();
            VarContext vars = varCollector.collectVars(cu);
            Map<MethodData, List<Effect>> effects = effectCollector.collectEffects(cu, vars);
            EffectInjectionModifier effectInjectionModifier = new EffectInjectionModifier(effects);
            effectInjectionModifier.visit(cu, null);
            String filePath = targetPath + key + ".java";
            try {
                FileWriterWrapper fw = new FileWriterWrapper(filePath);
                fw.write(cu.toString());
            } catch (Exception e) {
                System.out.println("Error writing to file: " + e);;
            }

        }
    }

    private void prepareAnalysisDirectory(String sourcePath, String targetPath) {
        try {
            // Source: https://stackoverflow.com/questions/29076439/java-8-copy-directory-recursively
            Path target = Paths.get(targetPath);
            Path source = Paths.get(sourcePath);
            if (Files.exists(target)) {
                Files.walk(target)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } else {
                Files.createDirectories(target);
            }
            copyFolder(
                    source,
                    target
            );
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void copyFolder(Path src, Path dest) throws IOException {
        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> copy(source, dest.resolve(src.relativize(source))));
        }
    }

    private void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
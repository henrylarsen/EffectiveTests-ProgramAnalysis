package org.effective.tests;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ClassCollector {
    /*
    The source code of these classes is stored in CompilationUnits
    rather than ClassOrInterfaceDeclarations because we need to add import statements for the analyzer
    */

    /* All test classes within the target directory and their corresponding CompilationUnits and classes under test.
        Key is the path of the class.
     */
    private Map<Path, TestData> testClasses;

    /* All source classes referenced by test classes within the target directory.
        Key is the class name, e.g., "FModders", to match the AnalyzeVisitor targetClass parameter
     */
    private Map<String, CompilationUnit> sourceClasses;

    public ClassCollector() {
        testClasses = new HashMap<>();
        sourceClasses = new HashMap<>();
    }

    public Map<Path, TestData> getTestClassData() {
        return testClasses;
    }

    public Map<String, CompilationUnit> getSourceClasses() {
        return sourceClasses;
    }

    /***
     * Collects all test classes marked with @EffectiveTest annotations and the source classes tested
     * @param targetPath path of the directory containing source and test classes
     */

    public ClassCollector collectClasses(String targetPath) {
        Map<String, CompilationUnit> allClasses = new HashMap();
        try {
            Path target = Paths.get(targetPath);
            if (Files.exists(target)) {
                Files.walk(target)
                        .filter(Files::isRegularFile)
                        .filter(file -> file.toString().endsWith(".java")).forEach(p -> {
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(Files.newInputStream(p));
                        allClasses.put(stripPathToClassName(p), cu);

                        List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

                        // Iterate over classes
                        for (ClassOrInterfaceDeclaration cd : classes) {
                        AnnotationExpr ae = cd.getAnnotationByName("EffectiveTest").orElse(null);
                            if (ae instanceof SingleMemberAnnotationExpr smae) {
                                String className = stripExtension(smae.getMemberValue().toString(), ".class");
                                // initializes source class entries to be filled in after the whole directory has been traversed
                                sourceClasses.put(className, null);
                                // only collect a test class if it contains a valid source class
                                testClasses.put(p, new TestData(cu, className));
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            // can't parse the root directory
        }
        for (Map.Entry<String, CompilationUnit> entry : sourceClasses.entrySet()) {
            String key = entry.getKey();
            CompilationUnit classCU = allClasses.get(key);
            if (classCU != null) {
                sourceClasses.put(key, classCU);
            }
        }
        return this;
    }

    /**
     * Strips a given extension from a string if it is present.
     */
    private static String stripExtension(String input, String extension) {
        if (input.endsWith(extension)) {
            input = input.substring(0, input.length() - extension.length());
        }
        return input;
    }

    public String stripPathToClassName(Path p) {
        String fileName = p.getFileName().toString();
        int extensionIndex = fileName.lastIndexOf(".java");
        if (extensionIndex != -1) {
            return fileName.substring(0, extensionIndex);
        }
        return fileName;
    }
}

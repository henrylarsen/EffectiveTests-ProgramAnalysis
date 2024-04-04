package org.effective.tests;

import com.github.javaparser.ast.CompilationUnit;

public class TestData {
    private final CompilationUnit testClass;

    // We only support one class under test per test class.
    private final String sourceClassName;

    public TestData(CompilationUnit testClass, String sourceClassName) {
        this.testClass = testClass;
        this.sourceClassName = sourceClassName;
    }

    public CompilationUnit getTestClass() {
        return testClass;
    }

    public String getSourceClassName() {
        return sourceClassName;
    }
}

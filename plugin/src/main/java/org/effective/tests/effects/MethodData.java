package org.effective.tests.effects;

import java.util.List;
import java.util.Objects;

public class MethodData {
    public String methodName;
    public List<String> paramTypes;
    public int methodLine;
    public MethodData(String methodName, List<String> paramTypes, int methodLine) {
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.methodLine = methodLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodData that = (MethodData) o;
        return methodLine == that.methodLine && Objects.equals(methodName, that.methodName) && Objects.equals(paramTypes, that.paramTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, paramTypes, methodLine);
    }

    @Override
    public String toString() {
        return methodName + ':' + paramTypes + ":" + methodLine;
    }
}

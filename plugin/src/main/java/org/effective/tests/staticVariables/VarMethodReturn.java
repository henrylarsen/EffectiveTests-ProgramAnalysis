package org.effective.tests.staticVariables;

import org.effective.tests.effects.MethodData;

import java.util.Objects;

public class VarMethodReturn extends VarType {
    public String classInstance;
    public MethodData method;

    public VarMethodReturn(String classInstance, MethodData method) {
        this.classInstance = classInstance;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarMethodReturn that = (VarMethodReturn) o;
        return Objects.equals(classInstance, that.classInstance) && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classInstance, method);
    }
}

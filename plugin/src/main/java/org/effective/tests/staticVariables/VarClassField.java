package org.effective.tests.staticVariables;

import java.util.Objects;

public class VarClassField extends VarType {
    public String classInstance;
    public String fieldName;

    public VarClassField(String classInstance, String fieldName) {
        this.classInstance = classInstance;
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return classInstance + ':' + fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarClassField varClassField = (VarClassField) o;
        return Objects.equals(classInstance, varClassField.classInstance) && Objects.equals(fieldName, varClassField.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classInstance, fieldName);
    }
}

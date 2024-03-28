package org.effective.tests.effects;

import java.util.Objects;

public class Getter extends Return {

    // store fieldName instead of Field because the field is by definition available
    private String fieldName;
    public Getter(String methodName, int methodLine, String fieldName) {
        super(methodName, methodLine);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Getter getter = (Getter) o;
        return this.lineNumber == getter.getLineNumber() &&
                Objects.equals(this.methodName, getter.getMethodName()) && Objects.equals(this.fieldName, getter.getFieldName());
    }
}

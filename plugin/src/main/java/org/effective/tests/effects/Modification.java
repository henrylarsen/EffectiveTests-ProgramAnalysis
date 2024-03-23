package org.effective.tests.effects;

import java.util.Objects;

/**
 * A modification of a field in a class.
 */
public class Modification extends Effect {
    private Field field;

    public Modification(String methodName, int lineNumber, Field f) {
        super(methodName, lineNumber);
        this.field = f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Effect effect = (Effect) o;
        return lineNumber == effect.lineNumber &&
                Objects.equals(methodName, effect.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.methodName, this.lineNumber, this.field);
    }

    public String toString() {
        return this.field.toString() + ":" + this.getLineNumber();
    }
}

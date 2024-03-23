package org.effective.tests.effects;


import java.util.Objects;

/**
 * A possible effect of a method.
 */
public abstract class Effect {
    protected String methodName;
    protected int lineNumber;

    public String getMethodName() {
        return this.methodName;
    };

    public int getLineNumber() { return this.lineNumber; }

    public Effect(String methodName, int lineNumber) {
        this.methodName = methodName;
        this.lineNumber = lineNumber;
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
        return Objects.hash(methodName, lineNumber);
    }

    public String toString() {
        return this.methodName + ":" + this.lineNumber;
    }

}

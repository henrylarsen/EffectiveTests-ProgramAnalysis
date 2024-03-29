package org.effective.tests.effects;


import java.util.Objects;

/**
 * A possible effect of a method.
 * The <i>testable</i> property indicates whether an effect is testable.
 * Test suite analysis should only be concerned with testable effects.
 */
public abstract class Effect implements Comparable<Effect>{
    protected String methodName;
    protected int lineNumber;
    protected boolean testable;

    public String getMethodName() {
        return this.methodName;
    };

    public int getLineNumber() { return this.lineNumber; }

    public Effect(String methodName, int lineNumber) {
        this.methodName = methodName;
        this.lineNumber = lineNumber;
        this.testable = true;
    }

    public Effect(String methodName, int lineNumber, boolean testable) {
        this.methodName = methodName;
        this.lineNumber = lineNumber;
        this.testable = testable;
    }

    public boolean isTestable() {
        return this.testable;
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

    @Override
    public int compareTo(Effect e) {
        return Integer.compare(this.lineNumber, e.lineNumber);
    }

}

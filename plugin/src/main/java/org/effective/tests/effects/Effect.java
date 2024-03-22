package org.effective.tests.effects;


/**
 * A possible effect of a method.
 */
public abstract class Effect {
    private String methodName;
    private int lineNumber;

    public String getMethodName() {
        return this.methodName;
    };

    public int getLineNumber() { return this.lineNumber; }

    public Effect(String methodName, int lineNumber) {
        this.methodName = methodName;
        this.lineNumber = lineNumber;
    }

}

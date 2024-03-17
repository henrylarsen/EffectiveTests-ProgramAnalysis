package org.comprehensive.staticanalysis.effects;

/**
 * A modification of a field in a class.
 */
public class Modification extends Effect {
    private Field field;

    public Modification(String methodName, int lineNumber, Field f) {
        super(methodName, lineNumber);
        this.field = f;
    }
}

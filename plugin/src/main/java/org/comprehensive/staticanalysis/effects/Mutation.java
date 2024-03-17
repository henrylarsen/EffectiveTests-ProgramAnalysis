package org.comprehensive.staticanalysis.effects;

/**
 * A mutation of any kind of object, including collections.
 */
public class Mutation extends Effect {
    private Object object;

    public Mutation(String methodName, int lineNumber, Object o) {
        super(methodName, lineNumber);
        this.object = o;
    }
}

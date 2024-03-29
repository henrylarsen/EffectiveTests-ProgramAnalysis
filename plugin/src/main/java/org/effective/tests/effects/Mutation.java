package org.effective.tests.effects;

/**
 * A mutation of any kind of object, including collections.
 */
public class Mutation extends Effect {
    private Object object;
    private String objectName;

    public Mutation(String methodName, int lineNumber, Object o, String objectName) {
        super(methodName, lineNumber);
        this.object = o;
        this.objectName = objectName;
    }

}

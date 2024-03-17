package org.comprehensive.staticanalysis.effects;

/**
 * An instance of a value being returned, unconcerned with the value itself.
 */
public class Return extends Effect {

    public Return(String methodName, int lineNumber) {
        super(methodName, lineNumber);
    }
}

package org.comprehensive.staticanalysis.effects;

/**
 * A modification of a field in a class.
 */
public class Modification extends Effect {
    private Field field;

    public Modification(Field f) {
        this.field = f;
    }
}

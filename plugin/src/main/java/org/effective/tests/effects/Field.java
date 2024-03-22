package org.effective.tests.effects;

/**
 * A simple representation of a field, unconcerned with its type.
 */
public class Field {
    private String name;
    private String className;

    public Field(String fieldName, String className) {
        this.name = fieldName;
        this.className = className;
    }
}

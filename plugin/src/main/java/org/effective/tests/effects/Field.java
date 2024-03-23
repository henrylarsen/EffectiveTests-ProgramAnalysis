package org.effective.tests.effects;

/**
 * A simple representation of a field, unconcerned with its type.
 */
public class Field {
    private String name;
    private boolean available;

    public Field(String fieldName) {
        this.name = fieldName;
        this.available = false;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailability(boolean b) {
        available = b;
    }

    public String getName() {
        return name;
    }
    public String toString() {
        return this.name;
    }
}

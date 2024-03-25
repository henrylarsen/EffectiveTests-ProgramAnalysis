package org.effective.tests.effects;

import java.util.Objects;

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

    public Field(String fieldName, boolean available) {
        this.name = fieldName;
        this.available = available;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return available == field.available &&
                Objects.equals(name, field.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, available);
    }

    public String toString() {
        return this.name;
    }
}

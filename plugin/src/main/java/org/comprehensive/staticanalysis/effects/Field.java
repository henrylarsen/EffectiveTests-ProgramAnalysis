package org.comprehensive.staticanalysis.effects;

/**
 * A simple representation of a field, unconcerned with its type.
 */
public class Field {
    private String name;
    private String className;
    /* could add a Boolean to indicate if the field is accessible
       if there's value down the line in tracking inaccessible fields
    */
    public Field(String fieldName, String className) {
        this.name = fieldName;
        this.className = className;
    }
}

package com.utils;

/**
 * 
 * Simple class for e.g. check boxes.
 *
 * @author Christoffer Hjeltnes Støle
 */
public class Option <T> {    
    String name;
    String displayName;
    T value;

    public Option(String name, String displayName, T defaultValue) {
        this.name = name;
        this.displayName = displayName;
        value = defaultValue;
    }
    
    public T getValue() {
        return value;
    }
    public void setValue(T value) {
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return displayName;
    }
    @Override
    public String toString() {
        return displayName;
    }
    
}

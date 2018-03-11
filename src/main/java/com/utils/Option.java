package com.utils;

/**
 * 
 * Simple class for e.g. show options.
 *
 * @author Christoffer Hjeltnes Støle
 */
public class Option {    
    String name;
    String displayName;
    boolean visible;

    public Option(String name, String displayName, boolean visibleByDefault) {
        this.name = name;
        this.displayName = displayName;
        visible = visibleByDefault;
    }
    
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
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

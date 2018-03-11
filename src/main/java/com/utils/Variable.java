package com.utils;

import java.util.Objects;

/**
 *
 * @author Christoffer Hjeltnes Støle
 * 
 * 
 * Class to represent MoBa variables.
 * 
 */
public class Variable implements Comparable <Variable> {
    Age age;
    Age ageUtils = new Age();
    String type;
    String name;
    String NULL_DISPLAY_NAME = "[none]";
    Boolean isLongitudinal;    
    String displayName;
    Constants constants = new Constants();
    
    
    public Variable (String variable, Boolean isLongitudinal) {
        this(variable);
        this.isLongitudinal = isLongitudinal;        
    }
    
    public Variable(String variable) {
        if (variable == null || variable.equals(NULL_DISPLAY_NAME)) {
            name = null;
        }
        else {
            name = variable;
        }        
        String ageString = ageUtils.getAgeString(variable);
        
        if (ageString != null) {
            isLongitudinal = false;
            age = new Age(ageString);
            if (ageString.equals("birth")) {
                type = variable.replace("Birth", "");
            }
            else {
                type = variable.replace(ageString, "");
            }
            
        }
        else {
            type = name;
        }        
    }
    
    private void generateDisplayName() {
        if (getName() == null) {;
            displayName = NULL_DISPLAY_NAME;
        }
        else {
            displayName = type;
            if (displayName.equals("N")) {            
            }
            else {
                displayName = displayName.replaceAll("([a-z])([A-Z])", "$1 $2"); // introduce spaces for camel case
                displayName = displayName.toLowerCase(); // default is lower case

                if (displayName.startsWith("father") || displayName.startsWith("mother") || displayName.startsWith("pregnancy")) {
                displayName = displayName.replaceFirst(" ", ": ");
                }
                if (displayName.contains("delta")) {                    
                    displayName = displayName.replaceAll("delta ([a-y]+) (.*)", "change in $1 ($2)");
                    displayName = displayName.replace("delta", "change in");
                }
                displayName = displayName.replace("bmi", "BMI");
                displayName = displayName.replace("sem", "SEM");

                displayName= displayName.replaceAll("^(.*) low$", "lower $1");
                displayName = displayName.replaceAll("^(.*) high$", "upper $1");

                if (hasAge()) {
                    displayName += " at " + age.getDescription();
                }
            }
        }
    }
    
    public String getName() {
        return name;
    }
    // TODO: cf. toString()
    public String getDisplayName() {
        if (displayName == null) {
            generateDisplayName();
        }
        return displayName;
    }
    @Override
    public String toString() {
        return getDisplayName();
    }    
    public String getType() {
        return type;
    }    
    public Age getAge() {
        return age;
    }
    public boolean hasAge() {
        return getAge() != null;
    }
    public Boolean isLongitudinal() {
        return isLongitudinal;
    }
    public boolean isNone() {
        return name == null;
    }
    
    
    @Override
    public int compareTo(Variable other) {       
        int alphanumericalComparison = new Alphanumerical(getType()).compareTo(new Alphanumerical(other.getType()));
        
//        System.out.println("getType(): " + getType());
//        System.out.println("other.getType(): " + other.getType());
        
        if (alphanumericalComparison != 0) { // found a ranking
            return alphanumericalComparison;
        }
        else if (getAge() != null && other.getAge() != null) { // the variables have the same type; compare the age
            return getAge().compareTo(other.getAge());
        }
        else {
            return alphanumericalComparison;
        }
    }
    
    /**
     * 
     * Compare the variable names.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Variable other = (Variable) object;
        return Objects.equals(getName(), other.getName());
    }
    @Override
    public int hashCode() {
        if (getName() != null) {
            return getName().hashCode();
        }
        else {
            return NULL_DISPLAY_NAME.hashCode();
        }
    }
    
}

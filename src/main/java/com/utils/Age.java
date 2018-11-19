package com.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Class to represent ages.
 * 
 * @author Christoffer Hjeltnes Støle* 
 * 
 * 
 */
public class Age implements Comparable <Age> {    
    Constants constants = new Constants();
    String [] recognisedUnits = constants.getTimeUnits();
    String [] recognisedUnitsShort = constants.getTimeUnitsShort();
    String recognisedUnitsPatternString;
    String description;
    String ageInDays;
    boolean standardiseUnits = false;
    Map <String, Double> dayEquivalents = new HashMap();
    
    /**
     * 
     * @param ageString 
     */
    public Age (String ageString) {
        this();
        description = ageString;
        if (ageString.equals("[none]")) { // "no age" option
            ageInDays = "-1";
        }
        else {
            standardizeDescription();
        }        
    }
    /**
     * Constructor for semi-static use.
     */
    public Age() {
        dayEquivalents.put("birth", 0.0);
        dayEquivalents.put("day", 1.0);
        dayEquivalents.put("week", 7.0);
        dayEquivalents.put("month", 30.4375);
        dayEquivalents.put("year", 365.25);
        dayEquivalents.put("d", dayEquivalents.get("day"));
        dayEquivalents.put("w", dayEquivalents.get("week"));
        dayEquivalents.put("m", dayEquivalents.get("month"));
        dayEquivalents.put("y", dayEquivalents.get("year"));
        createRecognisedUnitsPatternString();
    }
    
    /**
     * Returns a representation of the age.
     * 
     * @return 
     */
    public String getDescription() {
        return description;
    }
    
    public String getAgeInDays() {
        if (ageInDays == null) {
            toDays();
        }
        return ageInDays;
    }
    
    /**
     * 
     * Function for use in a semi-static context. Is the age positive?
     * 
     */    
    public boolean isValidAge(String ageString) {
        if (ageString.startsWith("-")) { // negative ages not allowed
                return false;
        }
        else if (getValueAndUnit(ageString) == null) {
            return false;
        }
        return true;
    }
    
    /**
     * 
     * Standardizes the description of the age.
     * 
     */
    public void standardizeDescription() {        
        description = description.replaceAll("(| )d$", " days");
        description = description.replaceAll("(| )w$", " weeks");
        description = description.replaceAll("(| )m$", " months");
        description = description.replaceAll("(| )y$", " years");
        
        if (standardiseUnits) {
            Double age = Double.parseDouble(getAgeInDays());
            if (age == 0.0) {
                description = "birth";
            }
            else if (age < 7) {
                description = age + " days";
            }
            else if (age < dayEquivalents.get("month")) {
                description = age/dayEquivalents.get("week") + " weeks";
            }
            else if (age < dayEquivalents.get("year")) {
                description = age/dayEquivalents.get("month") + " months";
            }
            else {
                description = age/dayEquivalents.get("year") + " years";
            }             
        }
        description = correctNumberUnitExpression(description);
    }
    
    /**
     * 
     * Returns the numerical value of the age and its unit in an array.
     * 
     * @param ageString
     * @return 
     */
    public String [] getValueAndUnit(String ageString) {
        Pattern pattern = Pattern.compile("([0-9.-]+)(?: |)(" + recognisedUnitsPatternString + ")(?:s|)$");
        Matcher patternMatch = pattern.matcher(ageString);
        if (patternMatch.find()) {
            String numberString = patternMatch.group(1);
            double number;
            if (numberString.contains("-")) { // age range
                String[] split = numberString.split("-");
                number = (Double.parseDouble(split[0]) + Double.parseDouble(split[1]))/2; // take the average
            }
            else {
                number = Double.parseDouble(numberString);
            }
             
            String unit = patternMatch.group(2).toLowerCase();
            if (!dayEquivalents.containsKey(unit)) {
                return null;
            }
            return new String [] {Double.toString(number), unit};
        }
        else {
            return null;
        }
    }
    
    /**
     * Finds the age in a variable.
     * 
     */
    public String getAgeString(String string) {
        if (string == null) {
            return null;
        }
        if (string.endsWith("Birth")) {
            return "birth";
        }
        else {
            Pattern pattern = Pattern.compile("[^0-9]+?(\\d{1,2}[wmy0-9-]+)");
            Matcher match = pattern.matcher(string);
            if (match.find()) { // time point entry
                 return match.group(1);
            }
            else {
                return null;
            }
        }
    }
    
    /** 
     * Converts a time period to days.
     * 
     */
    private void toDays() {        
        String [] valueAndUnit = getValueAndUnit(description);
        
        if (valueAndUnit != null) {
            double number = Double.parseDouble(valueAndUnit[0]);
            String unit = valueAndUnit[1];
            ageInDays = Double.toString(number*dayEquivalents.get(unit));
        }        
        else if (description.equals("birth")) {
            ageInDays = Double.toString(dayEquivalents.get(description));
        }     
    }
    
    /**
     * Fix spacing, incorrect plural forms etc.
     * 
     **/
    public String correctNumberUnitExpression(String expression) {
        createRecognisedUnitsPatternString();
        
        Pattern pattern = Pattern.compile("([0-9.-]+)(?: |)(" + recognisedUnitsPatternString + ")(?:s|)$");
        Matcher patternMatch = pattern.matcher(expression);
        String correctedExpression = expression;
        if (patternMatch.find()) {
            String numberString = patternMatch.group(1);
            String unit = patternMatch.group(2).toLowerCase();
            if (!numberString.equals("1") || numberString.contains("-")) {
                correctedExpression = numberString + " " + unit + "s";
            }
            else {
                correctedExpression = numberString + " " + unit;
            }
        }
        return correctedExpression;
    }
    
    /**
     * Aids in the construction of regex patterns.
     */
    private void createRecognisedUnitsPatternString() {
        if (recognisedUnitsPatternString == null){
            recognisedUnitsPatternString = "";
            for (String unit : recognisedUnits) {
                recognisedUnitsPatternString += unit + "|";
            }
            for (String unitShort : recognisedUnitsShort) {
                recognisedUnitsPatternString += unitShort + "|";
            }
            recognisedUnitsPatternString = recognisedUnitsPatternString.substring(0, recognisedUnitsPatternString.length()-1);
        }
    }
    
    @Override
    public String toString() {
        return getDescription();
    }

    /**
     * 
     * Compare the age in days.
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
        final Age other = (Age) object;
        return Objects.equals(getAgeInDays(), other.getAgeInDays());
    }
   
    @Override
    public int compareTo(Age other) {
        return Double.compare(Double.parseDouble(getAgeInDays()), Double.parseDouble(other.getAgeInDays()));
    }
    
}

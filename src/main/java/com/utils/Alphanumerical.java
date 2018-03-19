package com.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ChristofferHjeltnes
 * 
 * Class for intuitive sorting of alphanumerical strings.
 * 
 */
public class Alphanumerical implements Comparable<Alphanumerical> {
    String value;
    String format;
    Boolean digitsOnly;

    public Alphanumerical(String value) {
        this.value = value;
    }
    
    public Alphanumerical(String value, String format) {
        this.value = value;
        this.format = format;
    }
    
    @Override
    public int compareTo(Alphanumerical other) {
        String otherValue = other.getValue();
        
        if (value.startsWith("[last]")) {
            if (otherValue.startsWith("[last]")) {
                return 0;
            }
            else {
                return 1;
            }            
        }
        
        if (otherValue.startsWith("[last]")) {
                return -1;
        }
        
        if (format != null && other.getFormat() != null && format.equals(other.getFormat())) {
            if (format.equals("<letters><integers>")) {
                Pattern pattern = Pattern.compile("[a-zA-Z]+(\\d+)$");
                Matcher matchThis = pattern.matcher(value);
                Matcher matchOther = pattern.matcher(otherValue);
                if (matchThis.find() && matchOther.find()) {
                    int thisIntegerPart = Integer.parseInt(matchThis.group(1));
                    //System.out.println("value: " + value + ", integer part: " + thisIntegerPart);
                    int otherIntegerPart = Integer.parseInt(matchOther.group(1));
                    return Integer.compare(thisIntegerPart, otherIntegerPart);
                }
            }
        }
        
        Pattern patternNumerical = Pattern.compile("^([<>]|)([0-9]+)");
        Pattern patternNonNumerical = Pattern.compile("^([<>]|)([^0-9]+)");
        Matcher matchThisNumerical;
        Matcher matchThisNonNumerical;
        Matcher matchOtherNumerical;
        Matcher matchOtherNonNumerical;        
        
        String thisResidue = value;
        String otherResidue = otherValue;
        
        while (thisResidue.length() > 0 && otherResidue.length() > 0) { // compare all pairs of (non-) numerical sections of the alphanumerical
            matchThisNumerical = patternNumerical.matcher(thisResidue);
            matchThisNonNumerical = patternNonNumerical.matcher(thisResidue);
            matchOtherNumerical = patternNumerical.matcher(otherResidue);
            matchOtherNonNumerical = patternNonNumerical.matcher(otherResidue);
            
            if (matchThisNumerical.find()) { // this one is numerical
                if (!matchOtherNumerical.find()) { // the other one is not, and should come after
                    return -1;                    
                }
                else { // both are numerical                    
                    int comparison = Integer.compare(Integer.parseInt(matchThisNumerical.group(2)),
                            Integer.parseInt(matchOtherNumerical.group(2)));
                    if (comparison != 0) { // we have a ranking
                        return comparison;
                    }
                    else if (!matchThisNumerical.group(1).equals("")) { // we may still have a ranking because of operators
                        if (!matchOtherNumerical.group(1).equals("")) {
                            int operatorComparison = matchThisNonNumerical.group(1).compareTo(matchOtherNonNumerical.group(1));
                            if (comparison != 0) { // we have ranking
                                return comparison;
                            }
                        }
                        else if (matchThisNumerical.group(1).equals(">")) {
                            return 1;
                        }
                        else if (matchOtherNumerical.group(1).equals(">")) {
                            return -1;
                        }
                        else if (matchThisNumerical.group(1).equals("<")) {
                            return -1;
                        }
                        else if (matchOtherNumerical.group(1).equals("<")) {
                            return 1;
                        }
                    }
                    
                    thisResidue = thisResidue.replaceFirst(Pattern.quote(matchThisNumerical.group(2)), ""); // replacing a string, not regex
                    otherResidue = otherResidue.replaceFirst(Pattern.quote(matchOtherNumerical.group(2)), "");
                }
            }
            else if (matchThisNonNumerical.find()) { // this one is not numerical
                if (matchOtherNumerical.find()) { // the other one is, and should come first
                    return 1;                    
                }
                else if (matchOtherNonNumerical.find()) { // neither are numerical
                    int comparison = matchThisNonNumerical.group(2).compareTo(matchOtherNonNumerical.group(2));
                    if (comparison != 0) { // we have ranking
                        return comparison;
                    }
                    thisResidue = thisResidue.replaceFirst(Pattern.quote(matchThisNonNumerical.group(2)), "");
                    otherResidue = otherResidue.replaceFirst(Pattern.quote(matchOtherNonNumerical.group(2)), "");
                }
            }
        }
        return value.compareTo(otherValue); //regular string comparison
    }
    
    @Override
    public String toString() {
        if (digitsOnly == null) {
            digitsOnly = value.matches("[0-9]+");
        }
        if (hasDigitsOnly()) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            symbols.setGroupingSeparator(' ');

            DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
            //return String.format(Locale.FRENCH, "%,d", Integer.parseInt(value));
            return formatter.format(Integer.parseInt(value));
        }
        else {
            return value.replace("[last]", "");
        }
    }
    
    public String toNonBreakingString() {
        return toString().replace(" ", "&nbsp;");
    }
    
    /**
     * 
     * @return the raw string representation of the object; the string the object was created with
     */ 
    public String getValue() {
        return value;
    }
    
    public String getFormat() {
        return format;
    }
    
    public boolean hasDigitsOnly() {
        return digitsOnly;
    }
    
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
        final Alphanumerical other = (Alphanumerical) object;
        return Objects.equals(getValue(), other.getValue());
    }
    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
    
}

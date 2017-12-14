package com.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
        
        Pattern patternNumerical = Pattern.compile("^([0-9]+)");
        Pattern patternNonNumerical = Pattern.compile("^([^0-9]+)");
        Matcher matchThisNumerical;
        Matcher matchThisNonNumerical;
        Matcher matchOtherNumerical;
        Matcher matchOtherNonNumerical;        
        
        String thisResidue = value;
        String otherResidue = otherValue;
        
        while (thisResidue.length() > 0 && otherResidue.length() > 0) {
            matchThisNumerical = patternNumerical.matcher(thisResidue);
            matchThisNonNumerical = patternNonNumerical.matcher(thisResidue);
            matchOtherNumerical = patternNumerical.matcher(otherResidue);
            matchOtherNonNumerical = patternNonNumerical.matcher(otherResidue);
            
            if (matchThisNumerical.find()) {
                if (!matchOtherNumerical.find()) {
                    return -1;                    
                }
                else {
                    int comparison = Integer.compare(Integer.parseInt(matchThisNumerical.group(1)),
                            Integer.parseInt(matchOtherNumerical.group(1)));
                    if (comparison != 0) {
                        return comparison;
                    }
                    
                    thisResidue = thisResidue.replaceFirst(matchThisNumerical.group(1), "");
                    otherResidue = otherResidue.replaceFirst(matchOtherNumerical.group(1), "");
                }
            }
            else if (matchThisNonNumerical.find()) {
                if (matchOtherNumerical.find()) {
                    return 1;                    
                }
                else if (matchOtherNonNumerical.find()) {
                    int comparison = matchThisNonNumerical.group(1).compareTo(matchOtherNonNumerical.group(1));
                    if (comparison != 0) {
                        return comparison;
                    }
                    thisResidue = thisResidue.replaceFirst(matchThisNonNumerical.group(1), "");
                    otherResidue = otherResidue.replaceFirst(matchOtherNonNumerical.group(1), "");
                }
            }
        }
        return value.compareTo(otherValue);
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
    
    public String getValue() {
        return value;
    }
    
    public String getFormat() {
        return format;
    }
    
    public boolean hasDigitsOnly() {
        return digitsOnly;
    }
    
}

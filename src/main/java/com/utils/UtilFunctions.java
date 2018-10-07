package com.utils;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import elemental.json.JsonArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ChristofferHjeltnes
 */
public class UtilFunctions {
    Constants constants = new Constants();
    
    public List <Double> doubleList(List <String> list) {
        List <Double> doubleList = new ArrayList();
        for (String element : list) {
            if (element != null) {
                if (element.equals("<5")) {
                    doubleList.add(4.); // treat less than five as four                
                }
                else {
                    doubleList.add(Double.parseDouble(element));
                }
            }
            else {
                doubleList.add(null);
            }                
        }
        return doubleList;
    }
    public List <Integer> integerList(List <String> list) {
        List <Integer> integerList = new ArrayList();
        for (String element : list) {
            if (element != null) {
                integerList.add(Integer.parseInt(element));
            }
            else {
                integerList.add(null);
            }                
        }
        return integerList;
    }
    
    public String minInteger(List <String> values) {
        if (values.contains("0")) {
            return "0";
        }
        else if (values.contains("<5")) {
            return "<5";
        }
        else {
            return String.valueOf(Collections.min(integerList(values)));
        }
    }
    
    public String maxInteger(List <String> values) {
        List <Integer> numericalValues = new ArrayList();
        for (String value : values) {
            if (!value.equals("<5")) {
                numericalValues.add(Integer.parseInt(value));
            }
        }
        if (!numericalValues.isEmpty()) {
            int max = Collections.max(numericalValues);
            if (max == 0 && values.contains("<5")) {
                return "<5";
            }
            else {
                return String.valueOf(max);                
            }  
        }
        else {
            return "<5";
        }
    }
    
    public String maxDouble(List <String> values) {
        double max = Double.parseDouble(values.get(0));        
        for (int i = 1; i < values.size(); i++) {
            double value = Double.parseDouble(values.get(i)); 
            if (value > max) {
                max = value;
            }
        }
        return Double.toString(max);
    }
    public String minDouble(List <String> values) {
        double min = Double.parseDouble(values.get(0));        
        for (int i = 1; i < values.size(); i++) {
            double value = Double.parseDouble(values.get(i)); 
            if (value < min) {
                min = value;
            }
        }
        return Double.toString(min);
    }
    
    public List <String> jsonArrayToList(JsonArray array) {
        List <String> list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }
    
    
    public List <Alphanumerical> toAlphanumerical(Collection <String> collection) {
        List <Alphanumerical> alphanumericalList = new ArrayList();
        for (String string : collection) {
            alphanumericalList.add(new Alphanumerical(string));
        }
        return alphanumericalList;
    }
    public List <Variable> toVariable(Collection <String> collection) {
        List <Variable> variableList = new ArrayList();
        for (String string : collection) {
            variableList.add(new Variable(string));
        }
        return variableList;
    }
    
    public void toggleWindowVisibility(Window window, UI ui) {
        if (!window.isAttached()) { // is the window already open?
            ui.addWindow(window);
        }
        else{
            window.close();
        }
    }
    
    
}

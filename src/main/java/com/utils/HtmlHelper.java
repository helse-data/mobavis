package com.utils;

import java.util.List;

/**
 *
 * @author ChristofferHjeltnes
 */
public class HtmlHelper {
    
    public String bold(String string) {
        return "<b>" + string + "</b>";
    }
    public String italics(String string) {
        return "<i>" + string + "</i>";
    }
    
    public String link(String URL, String description) {
        return "<a href=\"" + URL + "\">" + description + "</a>";
    }
    
    public String listElement(String element) {
        return "<li>" + element + "</li>";
    }
    
    public String floatRight(String string) {
        return "<span style=\"float:right;\">" + string + "</span>";
        //return "<span class=\"v-align-right\">" + string + "</span>";
    }
    
    public String floatLeft(String string) {
        return "<span style=\"float:left;\">" + string + "</span>";
        //return "<span class=\"v-align-left\">" + string + "</span>";
    }
    
    public String createList(List <String> list) {
        String listString = "<ul>";
        
        for (String element : list) {
            listString += listElement(element);
        }
        listString += "</ul>";
        return listString;
    }
    
    public String colour(String string, String rgb) {
        return "<div style=\"color:rgb" + rgb + "\">" + string + "</div>";
    }
    
}

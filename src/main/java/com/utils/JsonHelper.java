package com.utils;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * JsonHelper provides auxiliary methods for the handling of JSON objects
 * and arrays.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public class JsonHelper {  
    
    /**
     * Adds an element to a JSON array, including null.
     * 
     * @param jsonArray
     * @param index
     * @param element 
     */
    public void set(JsonArray jsonArray, int index, String element) {
        if (element != null) { 
                jsonArray.set(index, element);                
            }
        else {
            jsonArray.set(index, Json.createNull()); // convert null to JSON null
        }
    }   

    /**
     * Converts a list of strings to a JSON array and places it in a JSON object.
     * 
     * @param jsonObject
     * @param key
     * @param list 
     */
    public void put(JsonObject jsonObject, String key, List <String> list) {
        JsonArray jsonArray = Json.createArray();
        for (String element : list) {            
            this.set(jsonArray, jsonArray.length(), element);                
        }        
        jsonObject.put(key, jsonArray);
    }
    
    /**
     * Converts a list of alphanumerical objects to a JSON array and places it in a JSON object.
     * 
     * @param jsonObject
     * @param key
     * @param list 
     */
    public void putAlphanumerical(JsonObject jsonObject, String key, List <Alphanumerical> list) {
        JsonArray jsonArray = Json.createArray();
        if (list == null) {
            jsonObject.put(key, Json.createNull());
        }
        else {
            for (Alphanumerical element : list) {            
                this.set(jsonArray, jsonArray.length(), element.toString());                
            }        
            jsonObject.put(key, jsonArray);
        }
        
    }
   
    /**
     * 
     * Converts a nested list of strings to a nested JSON array and places it in a JSON object.
     * 
     * @param jsonObject
     * @param key
     * @param doubleList 
     */
    public void putDoubleList(JsonObject jsonObject, String key, List <List <String>> doubleList) {
        JsonArray containerJsonArray = Json.createArray();
        for (List <String> list : doubleList) {
            JsonArray containedJsonArray = Json.createArray();
            for (String element : list) {
                this.set(containedJsonArray, containedJsonArray.length(), element); 
            }
            containerJsonArray.set(containerJsonArray.length(), containedJsonArray);
        }
        jsonObject.put(key, containerJsonArray);
    }    
}
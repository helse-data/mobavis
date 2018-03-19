package com.utils;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class JsonHelper {    
    public void set(JsonArray jsonArray, int index, String element) {
        if (element != null) { 
                jsonArray.set(index, element);                
            }
        else {
            jsonArray.set(index, Json.createNull()); // convert null to JSON null
        }
    }   

    public void put(JsonObject jsonObject, String key, List <String> list) {
        JsonArray jsonArray = Json.createArray();
        for (String element : list) {            
            this.set(jsonArray, jsonArray.length(), element);                
        }        
        jsonObject.put(key, jsonArray);
    }
    
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
    
    public String stringify(JsonObject jsonObject) {
        String string = jsonObject.toJson();
        string = string.replaceAll("\\{", "\n\t{");
        
        string = string.replaceAll("\\},", "},\n");
        string = string.replaceAll("\\],", "],\n");
        return string;
    }
    
}
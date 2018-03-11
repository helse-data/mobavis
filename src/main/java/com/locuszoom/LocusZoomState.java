package com.locuszoom;

import com.vaadin.shared.ui.JavaScriptComponentState;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

public class LocusZoomState extends JavaScriptComponentState {
    private JsonObject region;
    
    public void setRegion(JsonObject region) {
        this.region = region;
        System.out.println("Region: " + region);
    }
    
    public JsonObject getRegion() {
        return region;
    }    
}
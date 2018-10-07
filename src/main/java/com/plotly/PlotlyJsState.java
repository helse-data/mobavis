package com.plotly;

import com.vaadin.shared.ui.JavaScriptComponentState;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.Map;

public class PlotlyJsState extends JavaScriptComponentState {
    private JsonObject data;
    private JsonObject options;

    private Map mapData;
    private JsonObject setupData; // TODO: remove
    private JsonObject setup;
    private JsonObject resize;
    
    public void setSetup(JsonObject setup) {
        this.setup = setup;
    }
    
    public JsonObject getSetup() {
        return setup;
    }
    
    
    public void setSetupData(JsonObject setupData) {
        this.setupData = setupData;
    }
    
    public JsonObject getSetupData() {
        return setupData;
    }
    
    public void setData(JsonObject data) {
        this.data = data;
    }
    
    public JsonObject getData() {
        return data;
    }
    
    public void setOptions(JsonObject options) {
        this.options = options;
    }
    
    public JsonObject getOptions() {
        return options;
    }
    
    public Map getMapData() {
        return mapData;
    }

    public void setMapData(Map mapData) {
        this.mapData = mapData;
    }
    
    /**
     * Tells the plotly.js code to resize the plot. 
     * 
     * @param dummJsonObject
     */
    public void resize(JsonObject dummJsonObject) {
        System.out.println("Resize requested");
        this.resize = dummJsonObject;
    }
}
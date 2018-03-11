package com.plotly;

import com.vaadin.shared.ui.JavaScriptComponentState;
import elemental.json.JsonObject;

public class PlotlyJsState extends JavaScriptComponentState {
    private JsonObject data;
    private JsonObject setupData;
    private JsonObject size;
    
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
    
    public void setSize(JsonObject size) {
        this.size = size;
    }
    
    public JsonObject getSize() {
        return size;
    }
}
package com.plotly;

import elemental.json.JsonObject;

public class SNPPlotState extends PlotlyJsState {
    private JsonObject showStatus;   
   
    public void setShowStatus(JsonObject showStatus) {
        this.showStatus = showStatus;
    }
    
    public JsonObject getShowStatus() {
        return showStatus;
    }
    
}
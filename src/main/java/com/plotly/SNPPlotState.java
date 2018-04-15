package com.plotly;

import elemental.json.JsonObject;

public class SNPPlotState extends PlotlyJsState {
    private JsonObject plotOptions;   
   
    public void setPlotOptions(JsonObject plotOptions) {
        this.plotOptions = plotOptions;
    }
    
    public JsonObject getPlotOptions() {
        return plotOptions;
    }
    
}
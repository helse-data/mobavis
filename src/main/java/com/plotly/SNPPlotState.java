package com.plotly;

import elemental.json.JsonObject;

public class SNPPlotState extends PlotlyJsState {
    private JsonObject plotOptions; 
    private JsonObject activePlot;

    public JsonObject getActivePlot() {
        return activePlot;
    }

    public void setActivePlot(JsonObject activePlot) {
        this.activePlot = activePlot;
    }
   
    public void setPlotOptions(JsonObject plotOptions) {
        this.plotOptions = plotOptions;
    }
    
    public JsonObject getPlotOptions() {
        return plotOptions;
    }
    
}
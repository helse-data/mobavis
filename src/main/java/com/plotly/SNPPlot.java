// TODO: AA CI acts strangely on show/hide
// TODO: control clicks on legends

package com.plotly;

import com.vaadin.annotations.JavaScript;
import elemental.json.JsonObject;

//@JavaScript({"vaadin://plotly/SNPPlot.js"})
@JavaScript({
    "vaadin://plotly/SNPPlot.js",
    "vaadin://plotly/SNP2DPlot.js",
    "vaadin://plotly/SNP3DPlot.js"})
public class SNPPlot extends PlotlyJs {    
    boolean booleanDataVersion; // use a boolean version to avoid unnecessary counting
    boolean booleanShowStatusVersion;
    boolean booleanActivePlotVersion;
   
   public SNPPlot(JsonObject setup) {
       super(setup);
   }
    
   public SNPPlot() {
       this(null);
   }    

    @Override
    public void sendData(JsonObject data) {
        data.put("boolean version", booleanDataVersion);
        booleanDataVersion = !booleanDataVersion;
        getState().setData(data);
    }
    
    @Override
    public JsonObject getData() {
        return getState().getData();
    }
    
    public void sendPlotOptions(JsonObject showStatus) {
        showStatus.put("boolean version", booleanShowStatusVersion);
        booleanShowStatusVersion = !booleanShowStatusVersion;
        getState().setPlotOptions(showStatus);       
    }
    
    public JsonObject getPlotOptions() {
        return getState().getPlotOptions();       
    }
    
    public void setActivePlot(JsonObject activePlot) {
        activePlot.put("boolean version", booleanActivePlotVersion);
        booleanActivePlotVersion = !booleanActivePlotVersion;
        getState().setActivePlot(activePlot);       
    }
    
    public JsonObject getActivePlot() {
        return getState().getPlotOptions();       
    }
    
    @Override
    public SNPPlotState getState() {
        return (SNPPlotState) super.getState();
    }
    
    
    
}
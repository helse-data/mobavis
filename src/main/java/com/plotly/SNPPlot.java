// TODO: AA CI acts strangely on show/hide
// TODO: control clicks on legends

package com.plotly;

import com.vaadin.annotations.JavaScript;
import elemental.json.JsonObject;

@JavaScript({"vaadin://plotly/SNPPlot.js"})
public class SNPPlot extends PlotlyJs {    
    boolean booleanDataVersion; // use a boolean version to avoid unnecessary counting
    boolean booleanShowStatusVersion;
    
   public SNPPlot() {}
    

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
    
    public void sendShowStatus(JsonObject showStatus) {
        showStatus.put("boolean version", booleanShowStatusVersion);
        booleanShowStatusVersion = !booleanShowStatusVersion;
        getState().setShowStatus(showStatus);       
    }
    
    public JsonObject getShowStatus() {
        return getState().getShowStatus();       
    }
    
    @Override
    public SNPPlotState getState() {
        return (SNPPlotState) super.getState();
    }
    
    
    
}
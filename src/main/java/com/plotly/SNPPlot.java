package com.plotly;

import com.vaadin.annotations.JavaScript;
import elemental.json.JsonObject;

/**
 * Implements a Vaadin component for the 2D version of the plots
 * stratifying phenotypes by genotype.
 * 
 * @author Christoffer Hjeltnes Støle
 */
//@JavaScript({"vaadin://plotly/SNPPlot.js"})
@JavaScript({
    "vaadin://plotly/SNPPlot.js",
    "vaadin://plotly/SNP2DPlot.js",
    "vaadin://plotly/SNP3DPlot.js"})
public class SNPPlot extends PlotlyJs {    
    boolean booleanDataVersion; // use a boolean version to avoid unnecessary counting
    boolean booleanShowStatusVersion;
    boolean booleanActivePlotVersion;
   
    /**
     * 
     * @param setup - setup data
     */
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
    
    /**
     * Sets the options for the plot in the state object.
     * 
     * @param showStatus 
     */
    public void sendPlotOptions(JsonObject showStatus) {
        showStatus.put("boolean version", booleanShowStatusVersion);
        booleanShowStatusVersion = !booleanShowStatusVersion;
        getState().setPlotOptions(showStatus);       
    }
    
    /**
     * Returns the plot options stored in the state object.
     * @return 
     */
    public JsonObject getPlotOptions() {
        return getState().getPlotOptions();       
    }
    
    @Override
    public SNPPlotState getState() {
        return (SNPPlotState) super.getState();
    }
    
}